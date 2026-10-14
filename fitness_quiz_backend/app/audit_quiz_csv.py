import csv
import re
from collections import Counter, defaultdict
from pathlib import Path

CSV_FILE = Path(__file__).parent / "D:/root/fitness_quiz_backend/quiz_questions_generated.csv"
ISSUES_FILE = Path(__file__).parent / "D:/root/fitness_quiz_backend/quiz_audit_issues.csv"

REQUIRED_COLUMNS = [
    "question",
    "option_a",
    "option_b",
    "option_c",
    "option_d",
    "correct_answer",
    "difficulty",
    "topic",
    "source",
]

OPTION_COLUMNS = ["option_a", "option_b", "option_c", "option_d"]
VALID_DIFFICULTIES = {"easy", "medium", "hard"}


def normalize(value):
    """Normalize case, spacing and punctuation for duplicate comparisons."""
    value = str(value or "").casefold().strip()
    return re.sub(r"[^\w]", "", value, flags=re.UNICODE)


def get_answer_key(answer):
    """Recognize answer keys such as A, B, C, D, Option A or option_a."""
    raw = str(answer or "").strip().casefold()
    compact = re.sub(r"[^a-z0-9]", "", raw)

    mapping = {
        "a": "option_a",
        "optiona": "option_a",
        "b": "option_b",
        "optionb": "option_b",
        "c": "option_c",
        "optionc": "option_c",
        "d": "option_d",
        "optiond": "option_d",
    }
    return mapping.get(compact)


def main():
    if not CSV_FILE.exists():
        print(f"ERROR: CSV file not found:\n{CSV_FILE}")
        return

    with CSV_FILE.open(
        "r", encoding="utf-8-sig", newline=""
    ) as file:
        reader = csv.DictReader(file)

        headers = reader.fieldnames or []
        missing_columns = [
            col for col in REQUIRED_COLUMNS if col not in headers
        ]

        if missing_columns:
            print("ERROR: Required columns are missing:")
            for col in missing_columns:
                print(f"  - {col}")
            print(f"Columns found: {headers}")
            return

        rows = []
        for csv_row_number, row in enumerate(reader, start=2):
            rows.append((csv_row_number, row))

    issues = []
    question_locations = defaultdict(list)
    option_set_locations = defaultdict(list)

    topic_counts = Counter()
    difficulty_counts = Counter()
    source_counts = Counter()
    answer_format_counts = Counter()

    # First pass: duplicate detection and distributions.
    for row_number, row in rows:
        question = row.get("question", "")
        normalized_question = normalize(question)

        if normalized_question:
            question_locations[normalized_question].append(row_number)

        topic = (row.get("topic") or "").strip()
        difficulty = (row.get("difficulty") or "").strip()
        source = (row.get("source") or "").strip()

        topic_counts[topic or "(blank)"] += 1
        difficulty_counts[difficulty or "(blank)"] += 1
        source_counts[source or "(blank)"] += 1

        normalized_options = [
            normalize(row.get(col, "")) for col in OPTION_COLUMNS
        ]

        if all(normalized_options):
            option_set = tuple(sorted(normalized_options))
            option_set_locations[option_set].append(row_number)

    # Exact duplicates after normalization.
    duplicate_question_rows = set()

    for normalized_question, row_numbers in question_locations.items():
        if len(row_numbers) > 1:
            duplicate_question_rows.update(row_numbers)
            issues.append({
                "csv_row": ", ".join(map(str, row_numbers)),
                "check": "Duplicate question",
                "details": (
                    f"Same normalized question appears on CSV rows "
                    f"{row_numbers}"
                ),
            })

    # Identical sets of four options, even if the options are reordered.
    duplicate_option_set_rows = set()

    for row_numbers in option_set_locations.values():
        if len(row_numbers) > 1:
            duplicate_option_set_rows.update(row_numbers)
            issues.append({
                "csv_row": ", ".join(map(str, row_numbers)),
                "check": "Duplicate option set",
                "details": (
                    f"Same four normalized options appear on rows "
                    f"{row_numbers}"
                ),
            })

    # Second pass: validate every row.
    blank_cell_count = 0
    invalid_option_count = 0
    invalid_answer_count = 0
    invalid_difficulty_count = 0
    invalid_topic_count = 0
    invalid_source_count = 0

    for row_number, row in rows:
        question = (row.get("question") or "").strip()
        options = [
            (row.get(col) or "").strip() for col in OPTION_COLUMNS
        ]
        answer = (row.get("correct_answer") or "").strip()
        difficulty = (row.get("difficulty") or "").strip()
        topic = (row.get("topic") or "").strip()
        source = (row.get("source") or "").strip()

        # Check required fields for blanks.
        for col in REQUIRED_COLUMNS:
            if not str(row.get(col) or "").strip():
                blank_cell_count += 1
                issues.append({
                    "csv_row": row_number,
                    "check": "Blank required field",
                    "details": f"Column '{col}' is empty",
                })

        # Check for identical options within a question.
        normalized_options = [normalize(option) for option in options]
        nonblank_options = [
            option for option in normalized_options if option
        ]

        if len(nonblank_options) == 4 and len(set(nonblank_options)) != 4:
            invalid_option_count += 1
            issues.append({
                "csv_row": row_number,
                "check": "Repeated options",
                "details": "At least two options are identical after normalization",
            })

        # Check correct answer: letter/key or exact option text.
        answer_key = get_answer_key(answer)

        if answer_key:
            answer_is_valid = bool(
                normalize(row.get(answer_key, ""))
            )
            answer_format_counts["Letter/key"] += 1
        else:
            normalized_answer = normalize(answer)
            answer_is_valid = (
                bool(normalized_answer)
                and normalized_answer in nonblank_options
            )
            answer_format_counts["Option text"] += 1

        if not answer_is_valid:
            invalid_answer_count += 1
            issues.append({
                "csv_row": row_number,
                "check": "Correct answer not found in options",
                "details": (
                    f"correct_answer='{answer}'; "
                    f"options={options}"
                ),
            })

        # Check difficulty values.
        if difficulty.casefold() not in VALID_DIFFICULTIES:
            invalid_difficulty_count += 1
            issues.append({
                "csv_row": row_number,
                "check": "Invalid difficulty",
                "details": f"Unexpected value: '{difficulty}'",
            })

        if not topic:
            invalid_topic_count += 1
            issues.append({
                "csv_row": row_number,
                "check": "Missing topic",
                "details": "Topic is blank",
            })

        if not source:
            invalid_source_count += 1
            issues.append({
                "csv_row": row_number,
                "check": "Missing source",
                "details": "Source is blank",
            })

    # Write all detected issues to a separate report.
    if issues:
        with ISSUES_FILE.open(
            "w", encoding="utf-8-sig", newline=""
        ) as file:
            writer = csv.DictWriter(
                file,
                fieldnames=["csv_row", "check", "details"],
            )
            writer.writeheader()
            writer.writerows(issues)

    print("\n" + "=" * 55)
    print("       FITNESS QUIZ CSV AUDIT REPORT")
    print("=" * 55)

    print(f"CSV file                 : {CSV_FILE}")
    print(f"Total questions          : {len(rows)}")
    print(f"Exact/normalized duplicate questions: "
          f"{len(duplicate_question_rows)} affected rows")
    print(f"Duplicate option sets    : {len(duplicate_option_set_rows)} affected rows")
    print(f"Blank required cells     : {blank_cell_count}")
    print(f"Questions with repeated options: {invalid_option_count}")
    print(f"Invalid correct answers  : {invalid_answer_count}")
    print(f"Invalid difficulty values: {invalid_difficulty_count}")
    print(f"Missing topics           : {invalid_topic_count}")
    print(f"Missing sources          : {invalid_source_count}")

    print("\nDifficulty distribution:")
    for name, count in sorted(difficulty_counts.items()):
        print(f"  {name:15} {count:6}")

    print("\nTopic distribution:")
    for name, count in sorted(topic_counts.items()):
        print(f"  {name:15} {count:6}")

    print("\nSource distribution:")
    for name, count in source_counts.most_common():
        print(f"  {name:30} {count:6}")

    print("\nCorrect-answer storage format:")
    for name, count in answer_format_counts.items():
        print(f"  {name:15} {count:6}")

    if issues:
        print(f"\nAUDIT ISSUES FOUND: {len(issues)}")
        print(f"Details saved to:\n{ISSUES_FILE}")
    else:
        print("\nPASS: All automated structural checks passed.")
        print("No issues were recorded.")

    print("=" * 55)


if __name__ == "__main__":
    main()