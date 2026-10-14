from sqlalchemy import Boolean, Column, DateTime, Integer, String, Text, UniqueConstraint
from sqlalchemy.sql import func
from app.database import Base

class QuizQuestion(Base):
    __tablename__ = "quiz_questions"
    id = Column(Integer, primary_key=True, index=True)
    question = Column(Text, nullable=False)
    option_a = Column(String(500), nullable=False)
    option_b = Column(String(500), nullable=False)
    option_c = Column(String(500), nullable=False)
    option_d = Column(String(500), nullable=False)
    correct_answer = Column(String(1), nullable=False)
    difficulty = Column(String(20), nullable=False)
    topic = Column(String(100), nullable=False)
    source = Column(String(50), nullable=False, default="manual")
    used_count = Column(Integer, nullable=False, default=0)
    created_at = Column(DateTime(timezone=True),server_default=func.now(),nullable=False)
    is_active = Column(Boolean, nullable=False, default=True)

class UserQuizHistory(Base):
    __tablename__ = "user_quiz_history"
    __table_args__ = (UniqueConstraint("user_id","question_id",name="uq_user_quiz_history_user_question"),)
    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(String(100), nullable=False, index=True)
    question_id = Column(Integer, nullable=False, index=True)
    answered = Column(Boolean, nullable=False, default=False)
    correct = Column(Boolean, nullable=True)
    created_at = Column(DateTime(timezone=True),server_default=func.now(),nullable=False)