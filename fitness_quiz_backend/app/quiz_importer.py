import csv
from pathlib import Path

from sqlalchemy.exc import IntegrityError
from app.database import SessionLocal
from app.models import QuizQuestion

REQUIRED_COLUMNS = ["question","option_a","option_b","option_c","option_d","correct_answer","difficulty","topic","source",]
VALID_ANSWERS = {"A", "B", "C", "D"}
VALID_DIFFICULTIES = {"easy", "medium", "hard"}

def validate_row(row, row_number):
    errors = []
    for column in REQUIRED_COLUMNS:
        if not row.get(column, "").strip():
            errors.append(f"{column} is empty")
    correct_answer = row.get("correct_answer", "").strip().upper()
    if correct_answer not in VALID_ANSWERS:
        errors.append("correct_answer must be A, B, C, or D")
    difficulty = row.get("difficulty", "").strip().lower()
    if difficulty not in VALID_DIFFICULTIES:
        errors.append("difficulty must be easy, medium, or hard")
    if len(row.get("question", "").strip()) < 10:
        errors.append("question is too short")
    options = [w.get("option_a", "").strip(),row.get("option_b", "").strip(),row.get("option_c", "").strip(),row.get("option_d", "").strip(),]
    if len(set(options)) != 4:
        errors.append("options must be different")
    if errors:
        return False, f"Row {row_number}: " + "; ".join(errors)
    return True, None

def import_questions(csv_path):
    csv_path = Path(csv_path)
    if not csv_path.exists():
        raise FileNotFoundError(f"CSV file not found: {csv_path}")
    db = SessionLocal()
    imported = 0
    skipped = 0
    failed = 0
    try:
        with csv_path.open("r", encoding="utf-8-sig", newline="") as file:
            reader = csv.DictReader(file)
            if not reader.fieldnames:
                raise ValueError("CSV file has no header row")
            missing_columns = [
                column
                for column in REQUIRED_COLUMNS
                if column not in reader.fieldnames
            ]
            if missing_columns:
                raise ValueError("Missing CSV columns: " + ", ".join(missing_columns))
            for row_number, row in enumerate(reader, start=2):
                valid, error = validate_row(row, row_number)
                if not valid:
                    print(f"[SKIP] {error}")
                    skipped += 1
                    continue
                question_text = row["question"].strip()
                existing = (db.query(QuizQuestion).filter(QuizQuestion.question == question_text).first())
                if existing:
                    print(f"[SKIP] Row {row_number}: ""duplicate question")
                    skipped += 1
                    continue
                question = QuizQuestion(
                    question=question_text,option_a=row["option_a"].strip(),option_b=row["option_b"].strip(),
                    option_c=row["option_c"].strip(),option_d=row["option_d"].strip(),
                    correct_answer=row["correct_answer"].strip().upper(),difficulty=row["difficulty"].strip().lower(),
                    topic=row["topic"].strip().lower(),source=row["source"].strip() or "manual",used_count=0,is_active=True,)
                try:
                    db.add(question)
                    db.commit()
                    imported += 1
                except IntegrityError:
                    db.rollback()
                    print(f"[SKIP] Row {row_number}: ""database duplicate")
                    skipped += 1
        print("\n========== IMPORT COMPLETE ==========")
        print(f"Imported : {imported}")
        print(f"Skipped  : {skipped}")
        print(f"Failed   : {failed}")
        print("=====================================")
    finally:
        db.close()
if __name__ == "__main__":
    import_questions("quiz_questions.csv")