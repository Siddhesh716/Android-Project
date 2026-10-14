from app.database import SessionLocal
from app.models import QuizQuestion

questions = [
    {
        "question": "Which nutrient is primarily responsible for muscle repair and growth?",
        "option_a": "Vitamin C","option_b": "Protein","option_c": "Fiber","option_d": "Water",
        "correct_answer": "B","difficulty": "easy","topic": "nutrition","source": "curated"
    },
    {
        "question": "Which nutrient is the body's main source of energy during normal daily activity?",
        "option_a": "Protein","option_b": "Calcium","option_c": "Vitamin D", "option_d": "Carbohydrates","correct_answer": "D",
        "difficulty": "medium","topic": "nutrition","source": "curated"
    },
    {
        "question": "Which food is generally considered a good source of dietary fiber?",
        "option_a": "Butter","option_b": "Chicken breast","option_c": "Lentils","option_d": "Egg whites","correct_answer": "C",
        "difficulty": "easy","topic": "nutrition","source": "curated"
    },
    {
        "question": "Which vitamin is commonly associated with calcium absorption and bone health?",
        "option_a": "Vitamin D","option_b": "Vitamin C","option_c": "Vitamin B12","option_d": "Vitamin K","correct_answer": "A",
        "difficulty": "medium","topic": "nutrition","source": "curated"
    },
    {
        "question": "Which mineral plays an important role in normal muscle and nerve function?","option_a": "Fluoride",
        "option_b": "Iodine","option_c": "Selenium","option_d": "Magnesium","correct_answer": "D","difficulty": "medium",
        "topic": "nutrition","source": "curated"
    },
    {
        "question": "Which macronutrient provides the most energy per gram?","option_a": "Fat","option_b": "Protein",
        "option_c": "Carbohydrate","option_d": "Fiber","correct_answer": "A","difficulty": "hard","topic": "nutrition","source": "curated"
    },
    {
        "question": "Which food is a common source of healthy unsaturated fats?","option_a": "Candy","option_b": "Almonds",
        "option_c": "Soft drinks","option_d": "Refined sugar","correct_answer": "B","difficulty": "easy","topic": "nutrition","source": "curated"
    },
    {
        "question": "Why is hydration important during physical activity?","option_a": "It helps support normal fluid balance",
        "option_b": "It eliminates the need for food","option_c": "It guarantees maximum strength",
        "option_d": "It prevents all forms of fatigue","correct_answer": "A","difficulty": "medium","topic": "nutrition","source": "curated"
    },
    {
        "question": "Which nutrient is especially important for transporting oxygen in the blood?","option_a": "Calcium",
        "option_b": "Potassium","option_c": "Iron","option_d": "Sodium","correct_answer": "C","difficulty": "hard","topic": "nutrition",
        "source": "curated"
    },
    {
        "question": "Which meal choice generally provides a combination of protein and carbohydrates?","option_a": "Black coffee",
        "option_b": "Plain cooking oil", "option_c": "Table sugar", "option_d": "Oats with milk","correct_answer": "D",
        "difficulty": "easy","topic": "nutrition","source": "curated"
    },
    {
        "question": "Which exercise primarily targets the chest muscles?", "option_a": "Calf raise","option_b": "Bench press",
        "option_c": "Bicep curl","option_d": "Leg extension", "correct_answer": "B", "difficulty": "easy","topic": "workout",
        "source": "curated"
    },
    {
        "question": "What is generally the main purpose of a warm-up before exercise?", "option_a": "Replace the need for hydration",
        "option_b": "Maximize fatigue before training","option_c": "Prepare the body for physical activity",
        "option_d": "Eliminate the need for recovery","correct_answer": "C","difficulty": "medium","topic": "workout","source": "curated"
    },
    {
        "question": "Which exercise primarily trains the biceps?","option_a": "Leg press","option_b": "Calf raise","option_c": "Crunch",
        "option_d": "Bicep curl","correct_answer": "D", "difficulty": "easy", "topic": "workout", "source": "curated"
    },
    {
        "question": "Which exercise is commonly used to train the quadriceps?", "option_a": "Leg extension","option_b": "Bicep curl",
        "option_c": "Chest fly", "option_d": "Wrist curl", "correct_answer": "A", "difficulty": "easy","topic": "workout","source": "curated"
    },
    {
        "question": "What does proper exercise form primarily help with?","option_a": "Eliminating the need for rest",
        "option_b": "Performing movements safely and effectively","option_c": "Guaranteeing rapid muscle growth",
        "option_d": "Replacing proper nutrition","correct_answer": "B", "difficulty": "medium","topic": "workout", "source": "curated"
    },
    {
        "question": "Which type of exercise mainly improves cardiovascular endurance?", "option_a": "Running","option_b": "Bicep curling",
        "option_c": "Static stretching", "option_d": "Wrist rotation", "correct_answer": "A","difficulty": "easy",
        "topic": "workout","source": "curated"
    },
    {
        "question": "What is a repetition in resistance training?", "option_a": "A full week's training plan","option_b": "A period of sleep",
        "option_c": "One complete movement of an exercise","option_d": "A nutrition measurement","correct_answer": "C",
        "difficulty": "medium","topic": "workout","source": "curated"
    },
    {
        "question": "What is a set in resistance training?", "option_a": "A group of consecutive repetitions",
        "option_b": "A single warm-up movement", "option_c": "A complete training year", "option_d": "A type of protein supplement",
        "correct_answer": "A", "difficulty": "medium", "topic": "workout", "source": "curated"
    },
    {
        "question": "Which exercise primarily challenges the core muscles?", "option_a": "Calf raise", "option_b": "Bicep curl",
        "option_c": "Wrist curl", "option_d": "Plank", "correct_answer": "D","difficulty": "easy","topic": "workout","source": "curated"
    },
    {
        "question": "Why is gradually increasing training difficulty useful?", "option_a": "It removes the need for recovery",
        "option_b": "It guarantees no muscle soreness", "option_c": "It can provide a progressive training stimulus",
        "option_d": "It eliminates the need for proper technique", "correct_answer": "C", "difficulty": "hard",
        "topic": "workout", "source": "curated"
    },
    {
        "question": "Which factor is especially important for muscle recovery after demanding exercise?","option_a": "Skipping meals",
        "option_b": "Adequate rest", "option_c": "Avoiding all movement", "option_d": "Reducing sleep","correct_answer": "B",
        "difficulty": "easy", "topic": "recovery", "source": "curated"
    },
    {
        "question": "Why is sleep important for physical recovery?", "option_a": "It supports physiological recovery processes",
        "option_b": "It permanently prevents muscle soreness","option_c": "It replaces the need for nutrition",
        "option_d": "It guarantees improved performance", "correct_answer": "A","difficulty": "medium","topic": "recovery","source": "curated"
    },
    {
        "question": "What can excessive training without sufficient recovery increase the risk of?","option_a": "Permanent immunity to fatigue",
        "option_b": "Guaranteed muscle growth", "option_c": "Elimination of recovery needs","option_d": "Fatigue and reduced performance",
        "correct_answer": "D","difficulty": "medium", "topic": "recovery","source": "curated"
    },
    {
        "question": "What is one purpose of a cool-down after exercise?", "option_a": "Gradually transition toward a resting state",
        "option_b": "Immediately maximize exercise intensity", "option_c": "Replace the need for hydration","option_d": "Prevent all future injuries", "correct_answer": "A", "difficulty": "easy", "topic": "recovery","source": "curated"
    },
    {
        "question": "Why can rest days be useful in a training program?", "option_a": "They guarantee faster results",
        "option_b": "They eliminate the need for exercise", "option_c": "They allow recovery between demanding sessions",
        "option_d": "They replace balanced nutrition", "correct_answer": "C", "difficulty": "easy","topic": "recovery", "source": "curated"
    },
    {
        "question": "Which combination best supports recovery after regular exercise?","option_a": "Sleep deprivation and skipped meals",
        "option_b": "Maximum training every day", "option_c": "Avoiding fluids and food",
        "option_d": "Sleep, nutrition, hydration, and appropriate rest", "correct_answer": "D","difficulty": "hard",
        "topic": "recovery", "source": "curated"
    },
    {
        "question": "What does muscle soreness after unfamiliar exercise commonly indicate?","option_a": "The muscles experienced a new training stimulus", "option_b": "The exercise permanently damaged the muscles", "option_c": "The body no longer needs recovery",
        "option_d": "The workout was automatically perfect", "correct_answer": "A", "difficulty": "medium","topic": "recovery",
        "source": "curated"
    },
    {
        "question": "Why is hydration relevant to recovery?", "option_a": "It replaces the need for sleep","option_b": "It helps maintain normal fluid balance", "option_c": "It guarantees complete muscle repair", "option_d": "It replaces all dietary nutrients",
        "correct_answer": "B", "difficulty": "easy", "topic": "recovery", "source": "curated"
    },
    {
        "question": "What should generally happen if persistent exercise pain does not improve?","option_a": "Automatically increase training intensity", "option_b": "Ignore the pain completely", "option_c": "Consider professional medical evaluation",
        "option_d": "Stop eating until it disappears", "correct_answer": "C", "difficulty": "hard","topic": "recovery","source": "curated"
    },
    {
        "question": "Which factor can negatively affect recovery from exercise?", "option_a": "Insufficient sleep","option_b": "Adequate hydration","option_c": "Balanced nutrition", "option_d": "Appropriate rest", "correct_answer": "A", "difficulty": "medium",
        "topic": "recovery", "source": "curated"
    },
    {
        "question": "Which measurement is commonly used to estimate body mass relative to height?", "option_a": "Blood pressure",
        "option_b": "Resting heart rate", "option_c": "BMI", "option_d": "Body temperature", "correct_answer": "C",
        "difficulty": "easy", "topic": "general","source": "curated"
    },
    {
        "question": "What does progressive overload generally involve in resistance training?","option_a": "Gradually increasing training demands",
        "option_b": "Keeping training identical forever", "option_c": "Avoiding resistance completely","option_d": "Training only once per month",
        "correct_answer": "A","difficulty": "hard", "topic": "general", "source": "curated"
    },
    {
        "question": "Which activity is generally considered aerobic exercise?","option_a": "Single heavy lift",
        "option_b": "Static stretching","option_c": "Finger movement", "option_d": "Brisk walking","correct_answer": "D",
        "difficulty": "easy", "topic": "general","source": "curated"
    },
    {
        "question": "What does flexibility describe?", "option_a": "The amount of oxygen in the blood",
        "option_b": "The ability of joints and muscles to move through a range of motion", "option_c": "The number of calories in a meal",
        "option_d": "The speed of digestion", "correct_answer": "B", "difficulty": "medium","topic": "general","source": "curated"
    },
    {
        "question": "What does muscular endurance refer to?", "option_a": "The ability to sustain repeated muscular activity",
        "option_b": "The ability to sleep longer", "option_c": "The amount of body fat alone", "option_d": "The speed of digestion",
        "correct_answer": "A","difficulty": "medium","topic": "general","source": "curated"
    },
    {
        "question": "Which fitness component is most directly related to the ability to produce force?","option_a": "Flexibility",
        "option_b": "Balance", "option_c": "Reaction time", "option_d": "Muscular strength", "correct_answer": "D",
        "difficulty": "easy", "topic": "general","source": "curated"
    },
    {
        "question": "Why is consistency important in a fitness program?", "option_a": "It guarantees identical results for everyone",
        "option_b": "Regular training helps provide repeated training stimuli", "option_c": "It eliminates the need for recovery",
        "option_d": "It makes nutrition unnecessary","correct_answer": "B", "difficulty": "medium","topic": "general","source": "curated"
    },
    {
        "question": "Which factor can influence a person's fitness progress?","option_a": "Exercise duration alone",
        "option_b": "Body weight alone", "option_c": "Training, nutrition, recovery, and individual factors",
        "option_d": "One workout session alone", "correct_answer": "C","difficulty": "hard","topic": "general","source": "curated"
    },
    {
        "question": "What is balance in physical fitness?","option_a": "The ability to maintain body control and stability",
        "option_b": "The ability to lift the heaviest possible weight","option_c": "The ability to run continuously for hours",
        "option_d": "The ability to digest food quickly","correct_answer": "A", "difficulty": "easy","topic": "general","source": "curated"
    },
    {
        "question": "Why should exercise intensity generally match a person's current fitness level?","option_a": "It guarantees maximum results immediately", "option_b": "It removes the need for technique", "option_c": "It prevents all possible injuries",
        "option_d": "It helps make training appropriately challenging", "correct_answer": "D", "difficulty": "hard",
        "topic": "general", "source": "curated"
    }
]

db = SessionLocal()
inserted_count = 0
skipped_count = 0

try:
    for question_data in questions:
        existing_question = (db.query(QuizQuestion).filter(QuizQuestion.question== question_data["question"]).first())
        if existing_question:
            skipped_count += 1
            continue
        question = QuizQuestion(
            question=question_data["question"],option_a=question_data["option_a"],
            option_b=question_data["option_b"],option_c=question_data["option_c"],
            option_d=question_data["option_d"],correct_answer=question_data["correct_answer"],
            difficulty=question_data["difficulty"],topic=question_data["topic"],source=question_data["source"])
        db.add(question)
        inserted_count += 1
    db.commit()
    print(f"{inserted_count} curated quiz questions inserted.")
    print(f"{skipped_count} existing quiz questions skipped.")
finally:
    db.close()

# python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8001