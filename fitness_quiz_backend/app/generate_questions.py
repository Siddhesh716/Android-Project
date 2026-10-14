import csv
import random
from pathlib import Path


TOTAL_QUESTIONS = 100
OUTPUT_FILE = "quiz_questions_generated.csv"

random.seed()


def make_question(question, options, correct_answer, difficulty, topic):
    return {
        "question": question,
        "option_a": options[0],
        "option_b": options[1],
        "option_c": options[2],
        "option_d": options[3],
        "correct_answer": correct_answer,
        "difficulty": difficulty,
        "topic": topic,
        "source": "offline",
    }


def shuffle_options(options, correct_text):
    items = list(options)
    random.shuffle(items)

    correct_index = items.index(correct_text)
    answer = chr(ord("A") + correct_index)

    return items, answer


# ============================================================
# NUTRITION
# ============================================================

def nutrition_questions():
    questions = []

    foods = [
        ("chicken breast", "protein"),
        ("egg", "protein"),
        ("lentils", "protein and fiber"),
        ("oats", "carbohydrates and fiber"),
        ("brown rice", "carbohydrates"),
        ("banana", "carbohydrates"),
        ("apple", "fiber and carbohydrates"),
        ("almonds", "healthy fats"),
    ]

    for food, nutrient in foods:
        options = [
            nutrient,
            "Vitamin C only",
            "Water only",
            "Dietary sodium only",
        ]

        options, answer = shuffle_options(options, nutrient)

        questions.append(
            make_question(
                f"Which nutrient contribution is commonly associated with {food}?",
                options,
                answer,
                "easy",
                "nutrition",
            )
        )

    # Calorie calculations
    calorie_values = [
        (20, 4, 80, "protein"),
        (30, 4, 120, "carbohydrate"),
        (10, 9, 90, "fat"),
        (25, 4, 100, "protein"),
        (40, 4, 160, "carbohydrate"),
        (15, 9, 135, "fat"),
    ]

    for grams, kcal_per_gram, result, nutrient in calorie_values:
        wrong = [
            result + 20,
            result - 20 if result > 20 else result + 30,
            result + 40,
        ]

        options = [f"{result} kcal"] + [
            f"{value} kcal" for value in wrong
        ]

        correct_text = f"{result} kcal"

        options, answer = shuffle_options(options, correct_text)

        questions.append(
            make_question(
                f"Approximately how many calories are provided by {grams} g of {nutrient}?",
                options,
                answer,
                "medium",
                "nutrition",
            )
        )

    questions.extend([
        make_question(
            "Which macronutrient provides approximately 9 kcal per gram?",
            ["Protein", "Carbohydrate", "Fat", "Water"],
            "C",
            "easy",
            "nutrition",
        ),
        make_question(
            "Which food is generally a good source of dietary fiber?",
            ["Lentils", "Butter", "Sugar", "Soft drink"],
            "A",
            "easy",
            "nutrition",
        ),
        make_question(
            "Which vitamin is commonly associated with sunlight exposure?",
            ["Vitamin C", "Vitamin D", "Vitamin B12", "Vitamin K"],
            "B",
            "easy",
            "nutrition",
        ),
        make_question(
            "Which mineral is important for normal bone health?",
            ["Calcium", "Vitamin C", "Glucose", "Caffeine"],
            "A",
            "easy",
            "nutrition",
        ),
    ])

    return questions


# ============================================================
# WORKOUT
# ============================================================

def workout_questions():
    questions = []

    exercises = [
        ("push-up", "chest, shoulders, and triceps"),
        ("squat", "lower body"),
        ("pull-up", "back and pulling muscles"),
        ("crunch", "abdominal muscles"),
        ("calf raise", "calf muscles"),
        ("biceps curl", "biceps"),
        ("shoulder press", "shoulders and triceps"),
        ("lunge", "legs and glutes"),
    ]

    for exercise, target in exercises:
        options = [
            target,
            "only the forearms",
            "only the neck",
            "only the ankles",
        ]

        options, answer = shuffle_options(options, target)

        questions.append(
            make_question(
                f"Which muscle group or movement area is primarily associated with the {exercise}?",
                options,
                answer,
                "easy",
                "workout",
            )
        )

    # Progressive overload
    overload_variables = [
        "increasing resistance gradually",
        "increasing repetitions gradually",
        "increasing training volume gradually",
        "progressively increasing training demands",
    ]

    for value in overload_variables:
        options = [
            value,
            "avoiding all resistance",
            "stopping exercise permanently",
            "reducing activity every session",
        ]

        options, answer = shuffle_options(options, value)

        questions.append(
            make_question(
                "Which approach is consistent with progressive overload?",
                options,
                answer,
                "medium",
                "workout",
            )
        )

    # Training volume calculations
    calculations = [
        (3, 10, 20),
        (4, 8, 25),
        (5, 10, 15),
        (4, 12, 10),
        (3, 12, 20),
    ]

    for sets, reps, weight in calculations:
        result = sets * reps * weight

        options = [
            f"{result} kg",
            f"{result + 100} kg",
            f"{max(result - 50, 50)} kg",
            f"{result + 200} kg",
        ]

        correct_text = f"{result} kg"

        options, answer = shuffle_options(options, correct_text)

        questions.append(
            make_question(
                f"A workout uses {sets} sets of {reps} repetitions with {weight} kg. What is the total training volume?",
                options,
                answer,
                "hard",
                "workout",
            )
        )

    return questions


# ============================================================
# RECOVERY
# ============================================================

def recovery_questions():
    questions = []

    recovery_factors = [
        "adequate sleep",
        "appropriate nutrition",
        "manageable training load",
        "rest between demanding sessions",
    ]

    for factor in recovery_factors:
        options = [
            factor,
            "eliminating all rest",
            "training maximally every day",
            "avoiding fluids",
        ]

        options, answer = shuffle_options(options, factor)

        questions.append(
            make_question(
                "Which practice can support recovery from exercise?",
                options,
                answer,
                "easy",
                "recovery",
            )
        )

    questions.extend([
        make_question(
            "What does DOMS commonly stand for?",
            [
                "Delayed Onset Muscle Soreness",
                "Daily Oxygen Muscle System",
                "Dynamic Overload Movement Strength",
                "Delayed Oxygen Metabolism State",
            ],
            "A",
            "easy",
            "recovery",
        ),
        make_question(
            "Why can recovery days be useful in a training program?",
            [
                "They allow the body time to recover",
                "They eliminate the need for nutrition",
                "They guarantee immediate muscle growth",
                "They replace all exercise",
            ],
            "A",
            "easy",
            "recovery",
        ),
    ])

    return questions


# ============================================================
# SLEEP
# ============================================================

def sleep_questions():
    questions = []

    sleep_ranges = [
        "7–9 hours",
        "about 7–9 hours",
        "roughly 7–9 hours",
    ]

    for sleep_range in sleep_ranges:
        options = [
            sleep_range,
            "3–4 hours",
            "5–6 hours",
            "2 hours",
        ]

        options, answer = shuffle_options(options, sleep_range)

        questions.append(
            make_question(
                "How much sleep is generally recommended for most healthy adults?",
                options,
                answer,
                "easy",
                "sleep",
            )
        )

    questions.extend([
        make_question(
            "Which habit generally supports healthy sleep?",
            [
                "Maintaining a regular sleep schedule",
                "Changing bedtime every night",
                "Sleeping only on weekends",
                "Avoiding sleep after exercise",
            ],
            "A",
            "easy",
            "sleep",
        ),
        make_question(
            "What can insufficient sleep negatively affect?",
            [
                "Alertness and recovery",
                "Only height",
                "Only hair color",
                "Nothing related to exercise",
            ],
            "A",
            "medium",
            "sleep",
        ),
    ])

    return questions


# ============================================================
# HYDRATION
# ============================================================

def hydration_questions():
    questions = []

    situations = [
        "long-duration exercise in hot conditions",
        "heavy sweating",
        "endurance exercise",
        "exercise in a hot environment",
    ]

    for situation in situations:
        options = [
            "Paying attention to fluid replacement",
            "Avoiding all fluids",
            "Skipping recovery",
            "Removing all rest periods",
        ]

        correct_text = options[0]

        options, answer = shuffle_options(options, correct_text)

        questions.append(
            make_question(
                f"During {situation}, which practice is generally important?",
                options,
                answer,
                "medium",
                "hydration",
            )
        )

    questions.extend([
        make_question(
            "What is the primary fluid lost through normal sweating?",
            ["Water", "Protein", "Fiber", "Vitamin C"],
            "A",
            "easy",
            "hydration",
        ),
        make_question(
            "What can be lost in sweat besides water?",
            ["Electrolytes", "Fiber", "Protein only", "Vitamin C only"],
            "A",
            "easy",
            "hydration",
        ),
    ])

    return questions


# ============================================================
# GENERAL FITNESS
# ============================================================

def general_questions():
    questions = []

    definitions = [
        (
            "BMI",
            "Body Mass Index",
            ["Body Muscle Indicator", "Basic Metabolic Intake", "Body Movement Index"],
        ),
        (
            "flexibility",
            "available range of motion",
            ["maximum heart rate", "body weight", "daily calorie intake"],
        ),
        (
            "balance",
            "ability to control body position",
            ["ability to digest protein", "hydration level", "daily calorie intake"],
        ),
        (
            "cardiorespiratory endurance",
            "ability to sustain prolonged aerobic activity",
            ["maximum flexibility", "grip size", "body temperature"],
        ),
    ]

    for term, correct, wrongs in definitions:
        options = [correct] + wrongs
        options, answer = shuffle_options(options, correct)

        questions.append(
            make_question(
                f"What best describes {term}?",
                options,
                answer,
                "medium",
                "general",
            )
        )

    activities = [
        ("jogging", "cardiovascular exercise"),
        ("cycling", "cardiovascular exercise"),
        ("swimming", "cardiovascular exercise"),
        ("stretching", "flexibility-focused activity"),
    ]

    for activity, category in activities:
        options = [
            category,
            "a type of dietary supplement",
            "a sleep stage",
            "a hydration measurement",
        ]

        options, answer = shuffle_options(options, category)

        questions.append(
            make_question(
                f"How is {activity} generally classified in fitness?",
                options,
                answer,
                "easy",
                "general",
            )
        )

    return questions


# ============================================================
# VALIDATION
# ============================================================

def validate_question(question):
    required = [
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

    for field in required:
        if not question.get(field):
            return False

    options = [
        question["option_a"],
        question["option_b"],
        question["option_c"],
        question["option_d"],
    ]

    if len(set(options)) != 4:
        return False

    if question["correct_answer"] not in {"A", "B", "C", "D"}:
        return False

    if question["difficulty"] not in {"easy", "medium", "hard"}:
        return False

    return True


# ============================================================
# GENERATOR
# ============================================================

def generate_questions(total):
    generators = [
        nutrition_questions,
        workout_questions,
        recovery_questions,
        sleep_questions,
        hydration_questions,
        general_questions,
    ]

    all_questions = []

    for generator in generators:
        all_questions.extend(generator())

    if not all_questions:
        raise RuntimeError("No questions were generated.")

    valid_questions = [
        question
        for question in all_questions
        if validate_question(question)
    ]

    if len(valid_questions) < total:
        print(
            f"Only {len(valid_questions)} unique questions are available "
            f"from the current offline question bank."
        )
        total = len(valid_questions)

    random.shuffle(valid_questions)

    selected = valid_questions[:total]

    return selected


# ============================================================
# CSV
# ============================================================

def save_csv(questions):
    output_path = Path(OUTPUT_FILE)

    fieldnames = [
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

    with output_path.open(
        "w",
        encoding="utf-8-sig",
        newline=""
    ) as file:

        writer = csv.DictWriter(
            file,
            fieldnames=fieldnames
        )

        writer.writeheader()

        for question in questions:
            writer.writerow(question)

    print()
    print("==========================================")
    print(" OFFLINE QUESTION GENERATION COMPLETE")
    print("==========================================")
    print(f"Questions generated : {len(questions)}")
    print(f"CSV file            : {output_path.resolve()}")
    print("==========================================")


# ============================================================
# MAIN
# ============================================================

if __name__ == "__main__":
    print("Starting offline question generation...")
    print(f"Requested questions: {TOTAL_QUESTIONS}")

    questions = generate_questions(TOTAL_QUESTIONS)

    save_csv(questions)