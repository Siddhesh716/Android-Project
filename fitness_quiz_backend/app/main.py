from fastapi import Depends, FastAPI
from sqlalchemy import func
from sqlalchemy.orm import Session
from app.database import Base, engine, get_db
from app.models import QuizQuestion, UserQuizHistory

Base.metadata.create_all(bind=engine)
app = FastAPI(title="Fitness Quiz Backend",version="1.0.0")

@app.get("/")
def root():
    return {"status": "online","service": "Fitness Quiz Backend"}

@app.get("/health")
def health():
    return {"status": "healthy"}

def calculate_recommended_difficulty(db: Session,user_id: str):
    total_answered = (db.query(func.count(UserQuizHistory.id)).filter(UserQuizHistory.user_id == user_id,UserQuizHistory.answered.is_(True))
        .scalar())
    if not total_answered:
        return "easy", 0.0
    correct_answers = (db.query(func.count(UserQuizHistory.id))
        .filter(UserQuizHistory.user_id == user_id,UserQuizHistory.answered.is_(True),UserQuizHistory.correct.is_(True)).scalar())
    accuracy = (correct_answers / total_answered) * 100
    if accuracy >= 80:
        difficulty = "hard"
    elif accuracy >= 60:
        difficulty = "medium"
    else:
        difficulty = "easy"
    return difficulty, round(accuracy, 2)

def find_unseen_question(db: Session,user_id: str,topic: str,recommended_difficulty: str,seen_ids: list):
    query = (db.query(QuizQuestion).filter(QuizQuestion.topic == topic,QuizQuestion.is_active.is_(True)))
    if seen_ids:query = query.filter(~QuizQuestion.id.in_(seen_ids))
    candidate = (query.filter(QuizQuestion.difficulty ==recommended_difficulty).order_by(QuizQuestion.used_count.asc(),QuizQuestion.id.asc())
        .first())
    if candidate is not None:
        return candidate
    query = (db.query(QuizQuestion).filter(QuizQuestion.topic == topic,QuizQuestion.is_active.is_(True)))
    if seen_ids:query = query.filter(~QuizQuestion.id.in_(seen_ids))
    return (query.order_by(QuizQuestion.used_count.asc(),QuizQuestion.id.asc()).first())

@app.get("/quiz/next")
def get_next_question(user_id: str,topic: str,db: Session = Depends(get_db)):
    user_id = user_id.strip()
    topic = topic.strip().lower()
    if not user_id:
        return {"status": "empty","message": "User ID is required."}
    if not topic:
        return {"status": "empty","message": "Quiz topic is required."}
    recommended_difficulty, accuracy = (calculate_recommended_difficulty(db,user_id))
    seen_question_ids = (db.query(UserQuizHistory.question_id).filter(UserQuizHistory.user_id == user_id).all())
    seen_ids = [
        row[0]
        for row in seen_question_ids
    ]
    topic_order = [topic,"nutrition","workout","recovery","general"]
    topic_order = list(dict.fromkeys(topic_order))
    candidate = None
    for current_topic in topic_order:
        candidate = find_unseen_question(db=db,user_id=user_id,topic=current_topic,recommended_difficulty=recommended_difficulty,seen_ids=seen_ids)
        if candidate is not None:
            break
    if candidate is not None:
        candidate.used_count += 1
        history = UserQuizHistory(user_id=user_id,question_id=candidate.id,answered=False,correct=None)
        db.add(history)
        db.commit()
        return {"status": "success","id": candidate.id,"question": candidate.question,
            "options": {"A": candidate.option_a,"B": candidate.option_b,"C": candidate.option_c,"D": candidate.option_d
            },"difficulty": candidate.difficulty,"topic": candidate.topic,"source": candidate.source,"recommended_difficulty":recommended_difficulty,"accuracy_percentage":accuracy}
    return {
        "status": "empty",
        "message":"No new quiz question is available right now.",
        "recommended_difficulty":recommended_difficulty,
        "accuracy_percentage":accuracy
    }

@app.post("/quiz/answer")
def submit_answer(user_id: str,question_id: int,selected_answer: str,db: Session = Depends(get_db)):
    user_id = user_id.strip()
    selected_answer = selected_answer.strip().upper()
    history = (db.query(UserQuizHistory).filter(UserQuizHistory.user_id == user_id,UserQuizHistory.question_id == question_id,
            UserQuizHistory.answered.is_(False)).order_by(UserQuizHistory.id.desc()).first())
    if history is None:
        return {"status": "error","message":"Quiz question history not found."}
    question = (db.query(QuizQuestion).filter(QuizQuestion.id == question_id).first())
    if question is None:
        return {"status": "error","message":"Quiz question not found."}
    is_correct = (selected_answer == question.correct_answer)
    history.answered = True
    history.correct = is_correct
    db.commit()
    return {"status": "success","correct": is_correct,"correct_answer":question.correct_answer}

@app.get("/quiz/performance")
def get_performance(user_id: str,db: Session = Depends(get_db)):
    total = (db.query(func.count(UserQuizHistory.id)).filter(UserQuizHistory.user_id == user_id,UserQuizHistory.answered.is_(True))
        .scalar())
    correct = (db.query(func.count(UserQuizHistory.id)).filter(UserQuizHistory.user_id == user_id,UserQuizHistory.answered.is_(True),
            UserQuizHistory.correct.is_(True)).scalar())
    if total == 0:
        accuracy = 0.0
    else:
        accuracy = round((correct / total) * 100,2)
    return {"status": "success","user_id": user_id,"total_answered": total,"correct_answers": correct,"accuracy_percentage": accuracy}

@app.get("/quiz/difficulty")
def get_difficulty(user_id: str,db: Session = Depends(get_db)):
    difficulty, accuracy = (calculate_recommended_difficulty(db,user_id))
    return {"status": "success","recommended_difficulty": difficulty,"accuracy_percentage": accuracy}