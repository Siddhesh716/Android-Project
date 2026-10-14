import csv
import random
from pathlib import Path

TOTAL_QUESTIONS = 500
OUTPUT_FILE = Path("D:/root/fitness_quiz_backend/quiz_questions_generated.csv")
random.seed(42)

def normalize_text(text):
    return " ".join(str(text).lower().strip().split())

def make_question(question,options,correct_answer,difficulty,topic,):
    options = list(options)
    if len(options) != 4:
        raise ValueError("Every question must have exactly 4 options.")
    if correct_answer in {"A", "B", "C", "D"}:
        correct_text = options[ord(correct_answer) - ord("A")]
    else:
        correct_text = correct_answer
    if correct_text not in options:
        raise ValueError(f"Correct answer '{correct_text}' was not found in options.")
    random.shuffle(options)
    correct_index = options.index(correct_text)
    answer = chr(ord("A") + correct_index)
    return {
        "question": str(question).strip(),"option_a": str(options[0]).strip(),
        "option_b": str(options[1]).strip(),"option_c": str(options[2]).strip(),
        "option_d": str(options[3]).strip(),"correct_answer": answer,"difficulty": difficulty, "topic": topic, "source": "offline",
    }

def validate_question(question):
    required = ["question","option_a","option_b","option_c","option_d","correct_answer","difficulty","topic","source",]
    for field in required:
        if not question.get(field):
            return False
    options = [question["option_a"],question["option_b"],question["option_c"],question["option_d"],]
    if len(set(normalize_text(x) for x in options)) != 4:
        return False
    if question["correct_answer"] not in {"A","B","C","D",}:
        return False
    if question["difficulty"] not in {"easy","medium","hard",}:
        return False
    if len(question["question"].strip()) < 10:
        return False
    return True

def add_question(collection,question,options,correct_answer,difficulty,topic,):
    try:
        item = make_question(question,options,correct_answer,difficulty,topic,)
        if validate_question(item):
            collection.append(item)
    except Exception:
        pass

def add_unique_question(collection,seen,question,options,correct_answer,difficulty,topic,):
    key = normalize_text(question)
    if key in seen:
        return
    try:
        item = make_question(question,options,correct_answer,difficulty,topic,)
        if validate_question(item):
            collection.append(item)
            seen.add(key)
    except Exception:
        pass

def nutrition_questions():
    questions = []
    seen = set()
    facts = [
        (
            "Which nutrient is the body's primary source of energy?",
            "Carbohydrate",
            ["Protein", "Vitamin C", "Calcium"],
            "easy",
        ),
        (
            "Which nutrient provides approximately 9 kcal per gram?",
            "Fat",
            ["Protein", "Carbohydrate", "Water"],
            "easy",
        ),
        (
            "Which nutrients provide approximately 4 kcal per gram?",
            "Protein and carbohydrate",
            ["Fat and water", "Vitamin C and calcium", "Fiber and water"],
            "medium",
        ),
        (
            "Which nutrient is especially important for tissue repair?",
            "Protein",
            ["Water", "Sodium", "Vitamin D"],
            "easy",
        ),
        (
            "Which nutrient is a major source of dietary fiber?",
            "Plant foods",
            ["Table salt", "Butter", "Plain water"],
            "easy",
        ),
        (
            "Which vitamin is commonly associated with sunlight exposure?",
            "Vitamin D",
            ["Vitamin C", "Vitamin B12", "Vitamin K"],
            "easy",
        ),
        (
            "Which mineral is important for normal bones and teeth?",
            "Calcium",
            ["Sodium", "Iron", "Potassium"],
            "easy",
        ),
        (
            "Which mineral is involved in oxygen transport through hemoglobin?",
            "Iron",
            ["Calcium", "Sodium", "Potassium"],
            "medium",
        ),
        (
            "Which nutrient is important for normal fluid balance?",
            "Sodium",
            ["Vitamin C", "Fiber", "Protein"],
            "easy",
        ),
        (
            "Which vitamin is commonly found in citrus fruits?",
            "Vitamin C",
            ["Vitamin D", "Vitamin B12", "Vitamin K"],
            "easy",
        ),
        (
            "Which nutrient is abundant in many whole grains?",
            "Dietary fiber",
            ["Cholesterol", "Vitamin D", "Sodium only"],
            "easy",
        ),
        (
            "Which food is generally high in protein?",
            "Chicken breast",
            ["Apple", "Olive oil", "White sugar"],
            "easy",
        ),
        (
            "Which food is commonly considered a plant protein source?",
            "Lentils",
            ["Butter", "Fruit juice", "Table sugar"],
            "easy",
        ),
        (
            "Which food is commonly associated with omega-3 fatty acids?",
            "Fatty fish",
            ["White sugar", "White bread", "Table salt"],
            "medium",
        ),
        (
            "Which food commonly provides dietary fiber?",
            "Oats",
            ["Butter", "Chicken breast", "Egg whites"],
            "easy",
        ),
        (
            "Which food is a common source of potassium?",
            "Banana",
            ["Butter", "Sugar", "Table salt"],
            "easy",
        ),
        (
            "Which food is a common source of calcium?",
            "Milk",
            ["Apple", "White rice", "Chicken breast"],
            "easy",
        ),
        (
            "Which food is generally rich in unsaturated fats?",
            "Almonds",
            ["White sugar", "Table salt", "Rice cakes"],
            "easy",
        ),
        (
            "Which food commonly contains healthy unsaturated fats?",
            "Avocado",
            ["White sugar", "Table salt", "Plain water"],
            "easy",
        ),
        (
            "Which food is generally a source of complex carbohydrates?",
            "Brown rice",
            ["Butter", "Table salt", "Chicken breast"],
            "easy",
        ),
        (
            "Which food is commonly used as a source of plant protein?",
            "Beans",
            ["Sugar", "Butter", "Fruit juice"],
            "easy",
        ),
        (
            "Which nutrient helps form and maintain body tissues?",
            "Protein",
            ["Water only", "Sodium only", "Vitamin D only"],
            "medium",
        ),
        (
            "Which nutrient is important for normal immune function?",
            "Vitamin C",
            ["Water only", "Sodium only", "Dietary fiber only"],
            "medium",
        ),
        (
            "Which nutrient helps the body absorb vitamins A, D, E, and K?",
            "Dietary fat",
            ["Water", "Sodium", "Protein only"],
            "medium",
        ),
        (
            "Which vitamins are fat-soluble?",
            "A, D, E, and K",
            ["B and C only", "C and D only", "B1 and B2 only"],
            "medium",
        ),
        (
            "Which vitamins are generally water-soluble?",
            "B vitamins and vitamin C",
            ["A and D", "D and E", "A and K"],
            "medium",
        ),
        (
            "Which food group is generally rich in micronutrients and fiber?",
            "Fruits and vegetables",
            ["Refined sugar", "Table salt", "Cooking oil only"],
            "easy",
        ),
        (
            "Which choice is generally the least processed?",
            "Whole apple",
            ["Sugary soda", "Candy bar", "Refined sugar"],
            "easy",
        ),
        (
            "Which food is commonly used as a protein-rich breakfast option?",
            "Eggs",
            ["Table sugar", "Butter only", "Fruit juice"],
            "easy",
        ),
        (
            "Which food commonly provides both carbohydrate and fiber?",
            "Oats",
            ["Butter", "Chicken breast", "Egg whites"],
            "easy",
        ),
    ]

    for question, correct, wrongs, difficulty in facts:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "nutrition",
        )

    # Calorie calculations.
    for grams in range(10, 151, 5):
        calories = grams * 4

        question = (
            f"A food provides 4 kcal per gram. "
            f"Approximately how many calories are provided by {grams} g?"
        )

        options = [
            f"{calories} kcal",
            f"{calories + 20} kcal",
            f"{max(0, calories - 20)} kcal",
            f"{grams * 9} kcal",
        ]

        add_unique_question(
            questions,
            seen,
            question,
            options,
            f"{calories} kcal",
            "medium",
            "nutrition",
        )

    for grams in range(10, 151, 5):
        calories = grams * 9

        question = (
            f"A nutrient provides 9 kcal per gram. "
            f"Approximately how many calories are provided by {grams} g?"
        )

        options = [
            f"{calories} kcal",
            f"{calories + 45} kcal",
            f"{max(0, calories - 45)} kcal",
            f"{grams * 4} kcal",
        ]

        add_unique_question(
            questions,
            seen,
            question,
            options,
            f"{calories} kcal",
            "medium",
            "nutrition",
        )

    # Mixed macronutrient calculations.
    meals = []

    for protein in range(10, 101, 10):
        for carbs in [20, 40, 60, 80, 100]:
            for fat in [5, 10, 15, 20, 25]:
                meals.append((protein, carbs, fat))

    random.shuffle(meals)

    for protein, carbs, fat in meals[:180]:
        calories = (
            protein * 4
            + carbs * 4
            + fat * 9
        )

        question = (
            f"A meal contains {protein} g protein, "
            f"{carbs} g carbohydrate, and {fat} g fat. "
            "Approximately how many calories does it provide?"
        )

        options = [
            f"{calories} kcal",
            f"{calories + 40} kcal",
            f"{max(0, calories - 40)} kcal",
            f"{protein * 9 + carbs * 4 + fat * 4} kcal",
        ]

        add_unique_question(
            questions,
            seen,
            question,
            options,
            f"{calories} kcal",
            "hard",
            "nutrition",
        )

    # Portion comparisons.
    portion_foods = [
        ("oats", 4),
        ("rice", 4),
        ("protein-rich food", 4),
        ("fat-rich food", 9),
        ("carbohydrate-rich food", 4),
    ]

    for food, kcal_per_gram in portion_foods:
        for grams in [25, 50, 75, 100, 125, 150]:
            calories = grams * kcal_per_gram

            question = (
                f"A serving of {food} contains approximately "
                f"{grams} g and provides {kcal_per_gram} kcal per gram. "
                "What is the approximate energy content?"
            )

            options = [
                f"{calories} kcal",
                f"{calories + 25} kcal",
                f"{max(0, calories - 25)} kcal",
                f"{grams * (9 if kcal_per_gram == 4 else 4)} kcal",
            ]

            add_unique_question(
                questions,
                seen,
                question,
                options,
                f"{calories} kcal",
                "medium",
                "nutrition",
            )

    return questions


# ============================================================
# WORKOUT
# ============================================================

def workout_questions():
    questions = []
    seen = set()

    facts = [
        (
            "Which exercise primarily targets the chest, shoulders, and triceps?",
            "Push-up",
            ["Squat", "Calf raise", "Crunch"],
            "easy",
        ),
        (
            "Which exercise primarily targets the lower body?",
            "Squat",
            ["Push-up", "Biceps curl", "Crunch"],
            "easy",
        ),
        (
            "Which exercise is primarily a pulling movement?",
            "Pull-up",
            ["Push-up", "Crunch", "Calf raise"],
            "easy",
        ),
        (
            "Which exercise primarily trains the abdominal muscles?",
            "Crunch",
            ["Squat", "Calf raise", "Biceps curl"],
            "easy",
        ),
        (
            "Which activity is primarily cardiovascular?",
            "Jogging",
            ["Biceps curl", "Plank", "Calf raise"],
            "easy",
        ),
        (
            "Which exercise primarily trains the calf muscles?",
            "Calf raise",
            ["Crunch", "Push-up", "Pull-up"],
            "easy",
        ),
        (
            "Which movement is primarily a hip-hinge exercise?",
            "Deadlift",
            ["Crunch", "Push-up", "Calf raise"],
            "medium",
        ),
        (
            "Which exercise primarily trains the biceps?",
            "Biceps curl",
            ["Squat", "Crunch", "Calf raise"],
            "easy",
        ),
        (
            "Which exercise commonly trains the shoulders?",
            "Overhead press",
            ["Crunch", "Calf raise", "Glute bridge"],
            "easy",
        ),
        (
            "Which exercise commonly trains the glutes?",
            "Glute bridge",
            ["Crunch", "Push-up", "Biceps curl"],
            "easy",
        ),
        (
            "Which exercise is commonly used to train the back?",
            "Row",
            ["Crunch", "Calf raise", "Wrist curl"],
            "easy",
        ),
        (
            "Which movement primarily trains the triceps?",
            "Triceps extension",
            ["Calf raise", "Crunch", "Squat"],
            "easy",
        ),
        (
            "Which exercise is commonly used to train the hamstrings?",
            "Leg curl",
            ["Push-up", "Crunch", "Calf raise"],
            "easy",
        ),
        (
            "Which exercise can train the quadriceps?",
            "Lunge",
            ["Crunch", "Biceps curl", "Wrist curl"],
            "easy",
        ),
        (
            "Which exercise can train the core through anti-extension?",
            "Plank",
            ["Biceps curl", "Calf raise", "Wrist curl"],
            "medium",
        ),
        (
            "Which exercise is commonly classified as a compound movement?",
            "Squat",
            ["Biceps curl", "Wrist curl", "Leg extension"],
            "easy",
        ),
        (
            "Which exercise is commonly classified as an isolation movement?",
            "Biceps curl",
            ["Squat", "Deadlift", "Pull-up"],
            "easy",
        ),
        (
            "Which activity is useful for improving aerobic endurance?",
            "Cycling",
            ["Wrist curl", "Biceps curl", "Calf raise"],
            "easy",
        ),
        (
            "Which movement commonly trains the posterior chain?",
            "Deadlift",
            ["Crunch", "Wrist curl", "Front raise"],
            "medium",
        ),
        (
            "Which exercise primarily involves pushing the body away from the floor?",
            "Push-up",
            ["Pull-up", "Leg curl", "Biceps curl"],
            "easy",
        ),
        (
            "Which exercise primarily involves pulling the body toward a bar?",
            "Pull-up",
            ["Push-up", "Crunch", "Leg extension"],
            "easy",
        ),
        (
            "Which exercise is commonly used for shoulder abduction?",
            "Lateral raise",
            ["Crunch", "Calf raise", "Leg curl"],
            "medium",
        ),
        (
            "Which exercise is commonly used to train the hamstrings through a hip hinge?",
            "Romanian deadlift",
            ["Crunch", "Push-up", "Wrist curl"],
            "medium",
        ),
        (
            "Which movement is commonly used to train the quadriceps?",
            "Leg extension",
            ["Biceps curl", "Wrist curl", "Crunch"],
            "easy",
        ),
        (
            "Which movement is commonly used to train the calves?",
            "Standing calf raise",
            ["Bench press", "Crunch", "Biceps curl"],
            "easy",
        ),
        (
            "Which exercise commonly trains the chest with an external load?",
            "Bench press",
            ["Leg curl", "Calf raise", "Crunch"],
            "easy",
        ),
    ]

    for question, correct, wrongs, difficulty in facts:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "workout",
        )

    overload = [
        (
            "What does progressive overload mean?",
            "Gradually increasing training demands",
            [
                "Keeping training exactly the same forever",
                "Avoiding resistance",
                "Training only once per month",
            ],
            "medium",
        ),
        (
            "Which variable can be increased to create progressive overload?",
            "Resistance",
            ["Room temperature", "Shoe color", "Music volume"],
            "easy",
        ),
        (
            "Which variable can be increased during resistance training?",
            "Repetitions",
            ["Hair length", "Room color", "Playlist length"],
            "easy",
        ),
        (
            "Which variable can contribute to progressive overload?",
            "Sets",
            ["Shoe size", "Room color", "Playlist length"],
            "easy",
        ),
        (
            "Why should overload generally be gradual?",
            "To manage increasing training stress",
            [
                "To avoid all progress",
                "To eliminate sleep",
                "To guarantee identical results",
            ],
            "medium",
        ),
        (
            "What is a reasonable progression after a weight becomes manageable?",
            "Gradually increase an appropriate training demand",
            [
                "Immediately double every variable",
                "Stop exercising",
                "Remove all recovery",
            ],
            "medium",
        ),
        (
            "Which approach can improve resistance-training progression?",
            "Gradually increasing resistance when appropriate",
            [
                "Changing nothing indefinitely",
                "Removing all working sets",
                "Avoiding recovery",
            ],
            "medium",
        ),
        (
            "Which approach can contribute to progressive overload?",
            "Increasing repetitions while maintaining appropriate technique",
            [
                "Ignoring technique",
                "Eliminating rest periods",
                "Reducing every training demand",
            ],
            "medium",
        ),
        (
            "Which variable can be adjusted when progressing an exercise?",
            "Training volume",
            ["Shoe color", "Room lighting", "Playlist title"],
            "medium",
        ),
        (
            "What should generally remain a priority when increasing training load?",
            "Appropriate technique",
            ["Ignoring fatigue", "Removing sleep", "Eliminating recovery"],
            "medium",
        ),
    ]

    for question, correct, wrongs, difficulty in overload:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "workout",
        )

    # Training volume calculations.
    volume_values = []

    for sets in range(2, 8):
        for reps in [5, 8, 10, 12, 15]:
            for weight in [10, 15, 20, 25, 30, 35]:
                volume_values.append((sets, reps, weight))

    random.shuffle(volume_values)

    for sets, reps, weight in volume_values[:180]:
        volume = sets * reps * weight

        question = (
            f"A person performs {sets} sets of {reps} repetitions "
            f"using {weight} kg. What is the total training volume?"
        )

        options = [
            f"{volume} kg",
            f"{volume + weight} kg",
            f"{sets * weight} kg",
            f"{reps * weight} kg",
        ]

        add_unique_question(
            questions,
            seen,
            question,
            options,
            f"{volume} kg",
            "hard",
            "workout",
        )

    # Repetition calculations.
    for sets in range(2, 9):
        for reps in [5, 8, 10, 12, 15, 20]:
            total = sets * reps

            question = (
                f"A workout contains {sets} sets of {reps} repetitions. "
                "How many repetitions are completed in total?"
            )

            options = [
                str(total),
                str(total + reps),
                str(max(1, total - reps)),
                str(sets + reps),
            ]

            add_unique_question(
                questions,
                seen,
                question,
                options,
                str(total),
                "medium",
                "workout",
            )

    technique = [
        (
            "Why is proper exercise technique important?",
            "It helps control movement and reduce unnecessary injury risk",
            [
                "It guarantees maximum strength immediately",
                "It eliminates recovery",
                "It makes nutrition unnecessary",
            ],
            "medium",
        ),
        (
            "What is generally preferable to using poor form with a heavier weight?",
            "Using manageable resistance with controlled technique",
            [
                "Using maximum weight regardless of form",
                "Holding the breath indefinitely",
                "Skipping every exercise",
            ],
            "medium",
        ),
        (
            "What is a compound exercise?",
            "An exercise involving multiple joints and muscle groups",
            [
                "An exercise using only one joint",
                "A stretching-only movement",
                "A breathing exercise",
            ],
            "easy",
        ),
        (
            "What is an isolation exercise?",
            "An exercise emphasizing movement at one primary joint",
            [
                "An exercise involving every major joint",
                "A warm-up walk",
                "A sleep routine",
            ],
            "easy",
        ),
        (
            "Why combine compound and isolation exercises?",
            "They can provide different muscular and movement demands",
            [
                "They guarantee identical results",
                "They remove recovery needs",
                "They eliminate cardio",
            ],
            "medium",
        ),
        (
            "Why can controlled repetitions be useful?",
            "They help maintain movement technique",
            [
                "They guarantee maximum strength",
                "They remove the need for recovery",
                "They replace warm-ups",
            ],
            "medium",
        ),
        (
            "Why should resistance be appropriate for the individual's ability?",
            "To allow useful training while maintaining technique",
            [
                "To guarantee no soreness",
                "To eliminate all fatigue",
                "To avoid any adaptation",
            ],
            "medium",
        ),
        (
            "Why can warm-up activities be useful before demanding exercise?",
            "They can prepare the body for movement",
            [
                "They guarantee no injuries",
                "They replace all training",
                "They eliminate recovery needs",
            ],
            "easy",
        ),
    ]

    for question, correct, wrongs, difficulty in technique:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "workout",
        )

    return questions


# ============================================================
# RECOVERY
# ============================================================

def recovery_questions():
    questions = []
    seen = set()

    facts = [
        (
            "What is the main purpose of recovery after training?",
            "Allow the body to recover and adapt",
            [
                "Prevent all future soreness",
                "Replace every workout",
                "Eliminate sleep",
            ],
            "easy",
        ),
        (
            "Which activity can be appropriate for low-intensity recovery?",
            "Gentle walking",
            [
                "Maximum-effort sprinting",
                "Heavy one-repetition lifting",
                "All-out interval training",
            ],
            "easy",
        ),
        (
            "What does DOMS stand for?",
            "Delayed onset muscle soreness",
            [
                "Daily oxygen muscle system",
                "Dynamic overload movement sequence",
                "Direct output muscle strength",
            ],
            "medium",
        ),
        (
            "Which factor influences recovery?",
            "Sleep",
            ["Shoe color", "Music genre", "Room decoration"],
            "easy",
        ),
        (
            "Which factor influences recovery?",
            "Nutrition",
            ["Phone brightness", "Playlist length", "Room color"],
            "easy",
        ),
        (
            "Which factor influences recovery?",
            "Training load",
            ["Shoe brand", "Screen size", "Music volume"],
            "medium",
        ),
        (
            "Why can rest days be useful?",
            "They provide time for recovery and adaptation",
            [
                "They guarantee faster progress for everyone",
                "They eliminate nutrition",
                "They prevent every type of soreness",
            ],
            "easy",
        ),
        (
            "What can happen when muscles are trained intensely without adequate recovery?",
            "Recovery and training quality may be impaired",
            [
                "Performance always improves",
                "Sleep becomes unnecessary",
                "Training stress disappears",
            ],
            "medium",
        ),
        (
            "What is one useful recovery practice?",
            "Getting adequate sleep",
            [
                "Skipping sleep",
                "Training maximally every day",
                "Avoiding all fluids",
            ],
            "easy",
        ),
        (
            "Why can easier training days be included in a program?",
            "They can help manage accumulated training stress",
            [
                "They guarantee no soreness",
                "They eliminate all physical activity",
                "They replace nutrition",
            ],
            "medium",
        ),
        (
            "What is a reasonable response to persistent training fatigue?",
            "Review training load and recovery",
            [
                "Automatically increase intensity",
                "Remove all rest",
                "Reduce sleep",
            ],
            "medium",
        ),
        (
            "What should be considered after several demanding sessions?",
            "Whether adequate recovery has occurred",
            [
                "Whether to double training immediately",
                "Whether to eliminate sleep",
                "Whether to avoid all food",
            ],
            "easy",
        ),
        (
            "Why is recovery important for long-term training?",
            "It helps manage training stress and support adaptation",
            [
                "It makes exercise unnecessary",
                "It guarantees zero soreness",
                "It eliminates the need for nutrition",
            ],
            "medium",
        ),
        (
            "Which combination can support recovery?",
            "Sleep, nutrition, and appropriate training load",
            [
                "Maximum training and minimal sleep",
                "No fluids and no rest",
                "Training without food or sleep",
            ],
            "medium",
        ),
        (
            "What can excessive training stress combined with inadequate recovery cause?",
            "Reduced training quality and persistent fatigue",
            [
                "Guaranteed performance improvement",
                "Automatic strength gains",
                "Complete elimination of fatigue",
            ],
            "hard",
        ),
    ]

    for question, correct, wrongs, difficulty in facts:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "recovery",
        )

    scenarios = [
        (
            "An athlete has persistent fatigue and declining performance. "
            "What is a sensible first adjustment?",
            "Increase recovery and reduce excessive training stress",
            [
                "Add maximum-intensity sessions every day",
                "Remove all rest days",
                "Reduce sleep further",
            ],
            "hard",
        ),
        (
            "A person feels unusually tired after several hard training days. "
            "What should be considered?",
            "Whether recovery is adequate",
            [
                "Whether training should always be doubled",
                "Whether fluids should be avoided",
                "Whether sleep should be eliminated",
            ],
            "medium",
        ),
        (
            "Why might an easier training week be included?",
            "To reduce training stress and support recovery",
            [
                "To guarantee zero soreness",
                "To avoid all activity permanently",
                "To replace nutrition",
            ],
            "medium",
        ),
        (
            "What should be considered when planning repeated high-intensity sessions?",
            "Adequate recovery between demanding sessions",
            [
                "Removing every rest period",
                "Ignoring fatigue",
                "Keeping sleep as low as possible",
            ],
            "medium",
        ),
        (
            "What can support recovery after demanding resistance training?",
            "Adequate sleep and nutrition",
            [
                "More maximum-intensity sessions",
                "Skipping meals",
                "Avoiding all fluids",
            ],
            "medium",
        ),
        (
            "Why might training performance decrease when recovery is inadequate?",
            "Accumulated fatigue can affect training quality",
            [
                "Training stress disappears",
                "Sleep becomes unnecessary",
                "Nutrition becomes irrelevant",
            ],
            "hard",
        ),
        (
            "What can a deload-style period help accomplish?",
            "Reduce training stress while maintaining useful movement",
            [
                "Guarantee permanent strength gains",
                "Eliminate all exercise forever",
                "Remove the need for sleep",
            ],
            "hard",
        ),
        (
            "Which sign may suggest that training stress should be reviewed?",
            "Persistent fatigue with declining performance",
            [
                "Improved performance",
                "Better sleep",
                "Normal recovery",
            ],
            "medium",
        ),
        (
            "An athlete reports normal energy and stable performance. "
            "What does this suggest when considering recovery?",
            "Current recovery may be adequate",
            [
                "Recovery is definitely impossible",
                "More training is always required",
                "Sleep is irrelevant",
            ],
            "medium",
        ),
        (
            "A person increases training volume while sleep decreases. "
            "What should be monitored?",
            "Fatigue and recovery",
            [
                "Shoe color",
                "Music choice",
                "Room temperature only",
            ],
            "medium",
        ),
    ]

    for question, correct, wrongs, difficulty in scenarios:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "recovery",
        )

    # Recovery interval calculations.
    for rounds in range(2, 9):
        for seconds in [30, 45, 60, 75, 90, 120, 150]:
            total = rounds * seconds

            question = (
                f"If {rounds} recovery periods each last "
                f"{seconds} seconds, what is the combined "
                "recovery time?"
            )

            options = [
                f"{total} seconds",
                f"{total + seconds} seconds",
                f"{max(0, total - seconds)} seconds",
                f"{rounds + seconds} seconds",
            ]

            add_unique_question(
                questions,
                seen,
                question,
                options,
                f"{total} seconds",
                "medium",
                "recovery",
            )

    return questions


# ============================================================
# SLEEP
# ============================================================

def sleep_questions():
    questions = []
    seen = set()

    facts = [
        (
            "How many hours of sleep are generally recommended for most healthy adults?",
            "7–9 hours",
            ["3–4 hours", "5–6 hours", "10–12 hours"],
            "easy",
        ),
        (
            "Which sleep habit supports consistent sleep patterns?",
            "Keeping a regular sleep schedule",
            [
                "Changing bedtime every night",
                "Skipping sleep after exercise",
                "Using caffeine immediately before bed",
            ],
            "easy",
        ),
        (
            "Why is adequate sleep important for people who exercise?",
            "It supports recovery and normal physiological function",
            [
                "It eliminates rest days",
                "It guarantees faster muscle growth",
                "It replaces nutrition",
            ],
            "easy",
        ),
        (
            "What can insufficient sleep negatively affect?",
            "Alertness and physical performance",
            [
                "Only flexibility",
                "Only body temperature",
                "Nothing related to exercise",
            ],
            "medium",
        ),
        (
            "Why can a consistent bedtime be useful?",
            "It can support a regular sleep pattern",
            [
                "It guarantees maximum performance",
                "It eliminates recovery",
                "It replaces exercise",
            ],
            "easy",
        ),
        (
            "Why should athletes take sleep seriously?",
            "Sleep supports recovery and normal physiological processes",
            [
                "Sleep replaces exercise",
                "Sleep guarantees no injuries",
                "Sleep makes nutrition unnecessary",
            ],
            "easy",
        ),
        (
            "What is a sensible response to repeatedly poor sleep during heavy training?",
            "Review sleep and recovery before adding more training stress",
            [
                "Immediately increase intensity",
                "Remove all rest days",
                "Reduce sleep further",
            ],
            "medium",
        ),
        (
            "Which habit can make a sleep schedule more consistent?",
            "Going to bed at a similar time each night",
            [
                "Changing bedtime randomly",
                "Using stimulants before bed",
                "Skipping sleep on weekends",
            ],
            "easy",
        ),
        (
            "Which environmental factor can affect sleep quality?",
            "Light and noise",
            ["Shoe color", "Exercise brand", "Food packaging"],
            "easy",
        ),
        (
            "Why can caffeine close to bedtime be problematic for some people?",
            "It may interfere with sleep",
            [
                "It guarantees deeper sleep",
                "It replaces recovery",
                "It eliminates fatigue permanently",
            ],
            "medium",
        ),
        (
            "Why is sleep relevant to exercise recovery?",
            "Recovery processes continue during sleep",
            [
                "Exercise recovery stops during sleep",
                "Sleep replaces all nutrition",
                "Sleep eliminates all training stress",
            ],
            "medium",
        ),
        (
            "What should a person consider if poor sleep persists?",
            "Sleep habits and possible contributing factors",
            [
                "Only increasing exercise intensity",
                "Eliminating all rest",
                "Reducing sleep further",
            ],
            "medium",
        ),
    ]

    for question, correct, wrongs, difficulty in facts:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "sleep",
        )

    # Sleep duration scenarios.
    for hours in range(4, 15):
        if hours < 7:
            correct = (
                "The sleep duration is below the general adult recommendation"
            )
            wrongs = [
                "The duration is within the general recommendation",
                "Sleep is unnecessary",
                "More training is automatically required",
            ]
            difficulty = "easy"
        elif hours <= 9:
            correct = (
                "The sleep duration is within the general adult recommendation"
            )
            wrongs = [
                "The duration is always insufficient",
                "Sleep should always be reduced",
                "Recovery is unnecessary",
            ]
            difficulty = "easy"
        else:
            correct = (
                "The duration is above the general adult recommendation"
            )
            wrongs = [
                "The duration is always insufficient",
                "Sleep has no relationship with recovery",
                "Rest is unnecessary",
            ]
            difficulty = "medium"

        question = (
            f"An adult regularly sleeps about {hours} hours per night. "
            "Which statement best describes this duration?"
        )

        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "sleep",
        )

    scenarios = [
        (
            "A person trains regularly but consistently sleeps poorly. "
            "What should be considered?",
            "Improving sleep as part of recovery",
            [
                "Removing recovery days",
                "Increasing training immediately",
                "Ignoring fatigue",
            ],
            "medium",
        ),
        (
            "A runner increases mileage while reducing sleep. "
            "What is a reasonable concern?",
            "Recovery capacity may be reduced",
            [
                "Training stress disappears",
                "Performance is guaranteed to improve",
                "Rest becomes unnecessary",
            ],
            "medium",
        ),
        (
            "Why can sleep consistency be useful?",
            "It supports a regular sleep pattern",
            [
                "It guarantees maximum strength",
                "It removes the need for nutrition",
                "It replaces exercise",
            ],
            "easy",
        ),
        (
            "What should an athlete consider if poor sleep continues for several nights?",
            "Recovery and sleep habits",
            [
                "Adding maximum-intensity training",
                "Removing rest days",
                "Reducing sleep further",
            ],
            "medium",
        ),
        (
            "An athlete has adequate training but poor sleep quality. "
            "Which area should be reviewed?",
            "Sleep habits and recovery",
            [
                "Only shoe selection",
                "Only music selection",
                "Only clothing color",
            ],
            "easy",
        ),
        (
            "A person sleeps 5 hours while training heavily. "
            "What is a sensible concern?",
            "Recovery may be affected by insufficient sleep",
            [
                "Sleep is irrelevant",
                "More training is automatically required",
                "Recovery is unnecessary",
            ],
            "medium",
        ),
        (
            "A person sleeps consistently and reports good recovery. "
            "What does this indicate?",
            "The sleep routine may be supporting recovery",
            [
                "Sleep is never relevant",
                "Training should always be doubled",
                "Rest should be removed",
            ],
            "medium",
        ),
        (
            "Why can a stable wake time help some people?",
            "It can support a consistent sleep schedule",
            [
                "It guarantees perfect sleep",
                "It eliminates fatigue",
                "It replaces exercise",
            ],
            "easy",
        ),
    ]

    for question, correct, wrongs, difficulty in scenarios:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "sleep",
        )

    return questions


# ============================================================
# HYDRATION
# ============================================================

def hydration_questions():
    questions = []
    seen = set()

    facts = [
        (
            "What is the main fluid lost through sweat?",
            "Water",
            ["Protein", "Fiber", "Calcium"],
            "easy",
        ),
        (
            "Why is hydration important during exercise?",
            "It supports normal physiological function and temperature regulation",
            [
                "It guarantees maximum performance",
                "It replaces all nutrients",
                "It eliminates recovery",
            ],
            "easy",
        ),
        (
            "When should athletes pay particular attention to fluid replacement?",
            "During long-duration exercise in hot conditions",
            [
                "Only during sleep",
                "Only while sitting",
                "Never during exercise",
            ],
            "easy",
        ),
        (
            "What does sweat contain besides water?",
            "Electrolytes",
            ["Protein powder", "Dietary fiber", "Vitamin tablets"],
            "easy",
        ),
        (
            "During prolonged exercise in heat, what should be considered?",
            "Fluid replacement and electrolyte needs",
            [
                "Avoiding all fluids",
                "Removing rest periods",
                "Avoiding carbohydrates",
            ],
            "medium",
        ),
        (
            "Which environmental condition can increase attention to hydration?",
            "Hot weather",
            ["Dark rooms", "Quiet music", "Cold storage"],
            "easy",
        ),
        (
            "Which exercise condition can increase fluid loss?",
            "Heavy sweating",
            ["Reading", "Sleeping", "Sitting quietly"],
            "easy",
        ),
        (
            "Why can hot conditions increase hydration needs?",
            "They can increase sweat losses",
            [
                "They eliminate sweat",
                "They prevent fluid loss",
                "They make water unnecessary",
            ],
            "easy",
        ),
        (
            "What should an athlete consider after substantial sweat loss?",
            "Replacing fluids and considering electrolyte losses",
            [
                "Avoiding water",
                "Skipping recovery",
                "Avoiding all food",
            ],
            "medium",
        ),
        (
            "Which factor can affect fluid requirements during exercise?",
            "Environmental temperature",
            ["Shoe color", "Music choice", "Room decoration"],
            "medium",
        ),
        (
            "Which factor can influence sweat rate?",
            "Exercise intensity",
            ["Shoe brand", "Playlist length", "Screen brightness"],
            "medium",
        ),
        (
            "Which environmental factor can increase sweat losses?",
            "Humidity",
            ["Quiet music", "Dark clothing alone", "Room decoration"],
            "medium",
        ),
        (
            "Why are electrolytes relevant during prolonged sweating?",
            "Sweat contains electrolytes that can be lost",
            [
                "Sweat contains no minerals",
                "Electrolytes replace all calories",
                "Electrolytes eliminate fatigue",
            ],
            "medium",
        ),
        (
            "What is a sensible hydration approach during exercise?",
            "Adjust fluid intake to activity and environmental conditions",
            [
                "Avoid all fluids",
                "Use exactly the same amount regardless of conditions",
                "Ignore sweat losses",
            ],
            "medium",
        ),
    ]

    for question, correct, wrongs, difficulty in facts:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "hydration",
        )

    durations = list(range(20, 301, 10))

    for minutes in durations:
        if minutes <= 40:
            correct = (
                "Maintain normal hydration habits and consider activity conditions"
            )
            difficulty = "easy"
        elif minutes <= 90:
            correct = (
                "Pay attention to fluid intake during the session"
            )
            difficulty = "medium"
        else:
            correct = (
                "Pay particular attention to fluid and electrolyte replacement"
            )
            difficulty = "hard"

        question = (
            f"During a {minutes}-minute exercise session, "
            "which hydration consideration is appropriate?"
        )

        add_unique_question(
            questions,
            seen,
            question,
            [
                correct,
                "Avoid all fluids",
                "Ignore sweat losses",
                "Remove all recovery periods",
            ],
            correct,
            difficulty,
            "hydration",
        )

    scenarios = [
        (
            "An athlete is sweating heavily during a long outdoor run. "
            "What should receive particular attention?",
            "Fluid replacement",
            [
                "Avoiding all water",
                "Removing rest periods",
                "Skipping recovery",
            ],
        ),
        (
            "A runner trains for a long time in hot weather and sweats heavily. "
            "What besides water may need consideration?",
            "Electrolytes",
            [
                "Dietary fiber",
                "Vitamin tablets only",
                "Protein powder only",
            ],
        ),
        (
            "A person exercises in cool conditions without heavy sweating. "
            "What remains appropriate?",
            "Maintain normal hydration habits",
            [
                "Avoid all fluids",
                "Drink no water all day",
                "Stop eating carbohydrates",
            ],
        ),
        (
            "Why can hot weather increase fluid loss?",
            "It can increase sweating",
            [
                "It eliminates sweating",
                "It prevents fluid loss",
                "It makes water unnecessary",
            ],
        ),
        (
            "Why can long-duration exercise require more attention to hydration?",
            "Fluid losses can accumulate over time",
            [
                "Sweating always stops",
                "Water becomes unnecessary",
                "Recovery becomes irrelevant",
            ],
        ),
        (
            "An athlete finishes a long session with substantial sweat loss. "
            "What should be considered?",
            "Replacing fluids and considering electrolyte losses",
            [
                "Avoiding fluids",
                "Ignoring recovery",
                "Skipping all food",
            ],
        ),
        (
            "A person exercises in moderate conditions with little sweating. "
            "What is generally reasonable?",
            "Maintain normal hydration according to thirst and conditions",
            [
                "Avoid all fluids",
                "Drink no water for the entire day",
                "Ignore all hydration needs",
            ],
        ),
        (
            "Why can individual hydration needs differ?",
            "Sweat rate, duration, intensity, and environment can differ",
            [
                "Everyone loses exactly the same amount of fluid",
                "Temperature never matters",
                "Exercise duration never matters",
            ],
        ),
    ]

    for question, correct, wrongs in scenarios:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            "medium",
            "hydration",
        )

    return questions


# ============================================================
# GENERAL FITNESS
# ============================================================

def general_questions():
    questions = []
    seen = set()

    definitions = [
        (
            "What does BMI stand for?",
            "Body Mass Index",
            [
                "Body Movement Intensity",
                "Basic Muscle Indicator",
                "Body Metabolism Intake",
            ],
            "easy",
        ),
        (
            "What does cardiorespiratory endurance describe?",
            "The ability to sustain activity involving the heart and lungs",
            [
                "Maximum flexibility of one joint",
                "Ability to lift one maximum weight",
                "Ability to balance without movement",
            ],
            "medium",
        ),
        (
            "Which activity is primarily cardiovascular?",
            "Jogging",
            ["Biceps curl", "Crunch", "Calf raise"],
            "easy",
        ),
        (
            "What does flexibility refer to?",
            "Available range of motion around joints",
            [
                "Maximum lifting strength",
                "Ability to sprint",
                "Number of calories eaten",
            ],
            "easy",
        ),
        (
            "What does balance involve?",
            "Maintaining body position and control",
            [
                "Only increasing muscle size",
                "Only increasing running speed",
                "Only counting calories",
            ],
            "easy",
        ),
        (
            "Which combination represents several important fitness components?",
            "Aerobic fitness, resistance training, and flexibility or mobility",
            [
                "Only sprinting",
                "Only stretching",
                "Only resistance training",
            ],
            "medium",
        ),
        (
            "What is an important part of training consistency?",
            "Repeated training over time",
            [
                "One extremely hard workout",
                "Changing programs every day",
                "Avoiding recovery",
            ],
            "easy",
        ),
        (
            "Which approach can improve cardiovascular fitness and strength?",
            "Combining aerobic and resistance training",
            [
                "Only sitting",
                "Only stretching",
                "Avoiding exercise",
            ],
            "medium",
        ),
        (
            "What is mobility generally concerned with?",
            "The ability to move joints through useful ranges of motion",
            [
                "Only body weight",
                "Only calorie intake",
                "Only maximum lifting force",
            ],
            "easy",
        ),
        (
            "What does muscular strength describe?",
            "The ability to produce force",
            [
                "Ability to sleep longer",
                "Ability to digest food",
                "Ability to maintain hydration",
            ],
            "easy",
        ),
        (
            "What does muscular endurance describe?",
            "The ability to perform repeated contractions over time",
            [
                "Maximum height",
                "Ability to sleep",
                "Body temperature",
            ],
            "medium",
        ),
        (
            "What does body composition describe?",
            "The relative amounts of different tissues and components in the body",
            [
                "Only height",
                "Only running speed",
                "Only flexibility",
            ],
            "medium",
        ),
        (
            "Why is consistency important in fitness?",
            "Adaptation generally requires repeated training over time",
            [
                "One workout always determines the final result",
                "Recovery is unnecessary",
                "Exercise has no cumulative effect",
            ],
            "medium",
        ),
        (
            "Why should training programs include recovery?",
            "Recovery helps manage training stress and supports adaptation",
            [
                "Recovery prevents all soreness",
                "Recovery makes exercise unnecessary",
                "Recovery guarantees identical results",
            ],
            "medium",
        ),
        (
            "What is aerobic exercise primarily designed to train?",
            "Cardiorespiratory endurance",
            ["Only grip strength", "Only flexibility", "Only balance"],
            "easy",
        ),
        (
            "What is resistance training primarily used to develop?",
            "Muscular strength and endurance",
            ["Only sleep quality", "Only hydration", "Only flexibility"],
            "easy",
        ),
    ]

    for question, correct, wrongs, difficulty in definitions:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "general",
        )

    # BMI calculations.
    bmi_values = []

    for weight in range(45, 101, 5):
        for height_cm in range(150, 196, 5):
            bmi_values.append((weight, height_cm / 100))

    random.shuffle(bmi_values)

    for weight, height in bmi_values[:180]:
        bmi = round(weight / (height * height), 1)

        question = (
            f"A person weighs {weight} kg and is "
            f"{height:.2f} m tall. What is the approximate BMI?"
        )

        options = [
            f"{bmi}",
            f"{round(bmi + 1.5, 1)}",
            f"{round(max(1, bmi - 1.5), 1)}",
            f"{round(bmi + 3.0, 1)}",
        ]

        add_unique_question(
            questions,
            seen,
            question,
            options,
            f"{bmi}",
            "hard",
            "general",
        )

    # Time/block calculations.
    for minutes in range(20, 181, 10):
        for block in [5, 10, 15, 20, 30]:
            if minutes % block != 0:
                continue

            blocks = minutes // block

            question = (
                f"A workout lasts {minutes} minutes and is divided "
                f"into {block}-minute blocks. "
                "How many complete blocks are there?"
            )

            options = [
                str(blocks),
                str(blocks + 1),
                str(max(1, blocks - 1)),
                str(minutes),
            ]

            add_unique_question(
                questions,
                seen,
                question,
                options,
                str(blocks),
                "medium",
                "general",
            )

    scenarios = [
        (
            "A beginner wants to improve cardiovascular endurance. "
            "Which approach is generally appropriate?",
            "Gradually increase aerobic training while allowing recovery",
            [
                "Perform maximum effort every day",
                "Avoid aerobic activity",
                "Remove sleep",
            ],
            "medium",
        ),
        (
            "A runner can comfortably complete 2 km and wants to reach 5 km. "
            "What is generally appropriate?",
            "Gradually increase running volume with adequate recovery",
            [
                "Immediately run 20 km every day",
                "Stop training completely",
                "Avoid all rest",
            ],
            "medium",
        ),
        (
            "A person has adapted to a training program. What can be considered next?",
            "A gradual increase in an appropriate training demand",
            [
                "Immediately double every variable",
                "Eliminate recovery",
                "Stop exercising",
            ],
            "medium",
        ),
        (
            "What is a sensible way to improve training consistency?",
            "Use a manageable routine that can be repeated over time",
            [
                "Train maximally every day",
                "Change programs after every session",
                "Ignore recovery",
            ],
            "easy",
        ),
        (
            "A person combines running and resistance training. What is one benefit?",
            "Different aspects of fitness can be trained",
            [
                "Recovery becomes unnecessary",
                "Sleep becomes unnecessary",
                "Nutrition becomes irrelevant",
            ],
            "medium",
        ),
        (
            "A person performs high-volume resistance training, frequent running, "
            "and sleeps very little. What issue should be considered?",
            "Training load and recovery may be inadequate",
            [
                "More training is always better",
                "Sleep has no relevance",
                "Recovery is unnecessary",
            ],
            "hard",
        ),
        (
            "A person has limited time but wants a balanced fitness routine. "
            "What can help?",
            "Combining efficient aerobic and resistance sessions",
            [
                "Avoiding all exercise",
                "Training every muscle maximally daily",
                "Eliminating recovery",
            ],
            "medium",
        ),
        (
            "A person wants to improve general fitness over several months. "
            "What is generally important?",
            "Consistency with appropriate progression and recovery",
            [
                "One extremely hard workout",
                "Changing plans every day",
                "Avoiding rest",
            ],
            "medium",
        ),
        (
            "A person is new to exercise and wants sustainable progress. "
            "What is generally appropriate?",
            "Start with manageable training and progress gradually",
            [
                "Begin with maximum intensity every day",
                "Avoid all recovery",
                "Double every training variable immediately",
            ],
            "medium",
        ),
        (
            "Why can a balanced fitness program be useful?",
            "It can address different components of fitness",
            [
                "It guarantees identical results",
                "It removes the need for recovery",
                "It makes nutrition irrelevant",
            ],
            "medium",
        ),
        (
            "A person has persistent fatigue during training. What should be reviewed?",
            "Training load, recovery, and sleep",
            [
                "Only shoe color",
                "Only playlist length",
                "Only exercise clothing",
            ],
            "medium",
        ),
        (
            "Why can gradual progression be useful for beginners?",
            "It allows training demands to increase as the person adapts",
            [
                "It guarantees no soreness",
                "It eliminates all fatigue",
                "It removes the need for rest",
            ],
            "medium",
        ),
    ]

    for question, correct, wrongs, difficulty in scenarios:
        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "general",
        )

    return questions


# ============================================================
# ADDITIONAL PARAMETERIZED QUESTIONS
# ============================================================

def extra_parameterized_questions():
    questions = []
    seen = set()

    # --------------------------------------------------------
    # Nutrition calculations
    # --------------------------------------------------------

    for grams in range(10, 201, 5):
        calories = grams * 4

        question = (
            f"If a food provides 4 kcal per gram, "
            f"approximately how many calories are provided by {grams} g?"
        )

        correct = f"{calories} kcal"

        options = [
            correct,
            f"{calories + 20} kcal",
            f"{max(0, calories - 20)} kcal",
            f"{grams * 9} kcal",
        ]

        add_unique_question(
            questions,
            seen,
            question,
            options,
            correct,
            "medium",
            "nutrition",
        )

    for grams in range(10, 201, 5):
        calories = grams * 9

        question = (
            f"If a nutrient provides 9 kcal per gram, "
            f"approximately how many calories are provided by {grams} g?"
        )

        correct = f"{calories} kcal"

        options = [
            correct,
            f"{calories + 45} kcal",
            f"{max(0, calories - 45)} kcal",
            f"{grams * 4} kcal",
        ]

        add_unique_question(
            questions,
            seen,
            question,
            options,
            correct,
            "medium",
            "nutrition",
        )

    # --------------------------------------------------------
    # Workout repetition calculations
    # --------------------------------------------------------

    for sets in range(2, 10):
        for reps in [5, 8, 10, 12, 15, 20]:
            total = sets * reps

            question = (
                f"A workout contains {sets} sets of {reps} repetitions. "
                "How many repetitions are completed in total?"
            )

            correct = str(total)

            options = [
                correct,
                str(total + reps),
                str(max(1, total - reps)),
                str(sets + reps),
            ]

            add_unique_question(
                questions,
                seen,
                question,
                options,
                correct,
                "medium",
                "workout",
            )

    # --------------------------------------------------------
    # Workout volume calculations
    # --------------------------------------------------------

    for sets in range(2, 7):
        for reps in [5, 8, 10, 12, 15]:
            for weight in [10, 15, 20, 25, 30]:
                volume = sets * reps * weight

                question = (
                    f"A person performs {sets} sets of {reps} repetitions "
                    f"using {weight} kg. What is the total training volume?"
                )

                correct = f"{volume} kg"

                options = [
                    correct,
                    f"{volume + weight} kg",
                    f"{sets * weight} kg",
                    f"{reps * weight} kg",
                ]

                add_unique_question(
                    questions,
                    seen,
                    question,
                    options,
                    correct,
                    "hard",
                    "workout",
                )

    # --------------------------------------------------------
    # Recovery interval calculations
    # --------------------------------------------------------

    for rounds in range(2, 10):
        for seconds in [30, 45, 60, 75, 90, 120]:
            total = rounds * seconds

            question = (
                f"If {rounds} recovery periods each last "
                f"{seconds} seconds, what is the combined "
                "recovery time?"
            )

            correct = f"{total} seconds"

            options = [
                correct,
                f"{total + seconds} seconds",
                f"{max(0, total - seconds)} seconds",
                f"{rounds + seconds} seconds",
            ]

            add_unique_question(
                questions,
                seen,
                question,
                options,
                correct,
                "medium",
                "recovery",
            )

    # --------------------------------------------------------
    # Sleep scenarios
    # --------------------------------------------------------

    for hours in range(4, 15):
        if hours < 7:
            correct = (
                "The duration is below the general adult recommendation"
            )
            wrongs = [
                "The duration is within the general recommendation",
                "Sleep is unnecessary",
                "More training is automatically required",
            ]
            difficulty = "easy"
        elif hours <= 9:
            correct = (
                "The duration is within the general adult recommendation"
            )
            wrongs = [
                "The duration is always insufficient",
                "Sleep should always be reduced",
                "Recovery is unnecessary",
            ]
            difficulty = "easy"
        else:
            correct = (
                "The duration is above the general adult recommendation"
            )
            wrongs = [
                "The duration is always insufficient",
                "Sleep has no relationship with recovery",
                "Rest is unnecessary",
            ]
            difficulty = "medium"

        question = (
            f"An adult regularly sleeps about {hours} hours per night. "
            "Which statement best describes this duration?"
        )

        add_unique_question(
            questions,
            seen,
            question,
            [correct] + wrongs,
            correct,
            difficulty,
            "sleep",
        )

    # --------------------------------------------------------
    # Hydration duration
    # --------------------------------------------------------

    for minutes in range(20, 301, 10):
        if minutes <= 40:
            correct = (
                "Maintain normal hydration habits and consider activity conditions"
            )
            difficulty = "easy"
        elif minutes <= 90:
            correct = (
                "Pay attention to fluid intake during the session"
            )
            difficulty = "medium"
        else:
            correct = (
                "Pay particular attention to fluid and electrolyte replacement"
            )
            difficulty = "hard"

        question = (
            f"During a {minutes}-minute exercise session, "
            "which hydration consideration is appropriate?"
        )

        add_unique_question(
            questions,
            seen,
            question,
            [
                correct,
                "Avoid all fluids",
                "Ignore sweat losses",
                "Remove all recovery periods",
            ],
            correct,
            difficulty,
            "hydration",
        )

    # --------------------------------------------------------
    # General time calculations
    # --------------------------------------------------------

    for minutes in range(20, 241, 10):
        for block in [5, 10, 15, 20, 30, 40]:
            if minutes % block != 0:
                continue

            blocks = minutes // block

            question = (
                f"A workout lasts {minutes} minutes and is divided "
                f"into {block}-minute blocks. "
                "How many complete blocks are there?"
            )

            correct = str(blocks)

            options = [
                correct,
                str(blocks + 1),
                str(max(1, blocks - 1)),
                str(minutes),
            ]

            add_unique_question(
                questions,
                seen,
                question,
                options,
                correct,
                "medium",
                "general",
            )

    return questions

# ============================================================
# DISTRIBUTION TARGETS
# ============================================================

TOPIC_TARGETS = {
    "nutrition": 100,
    "workout": 100,
    "general": 80,
    "recovery": 80,
    "hydration": 70,
    "sleep": 70,
}

DIFFICULTY_TARGETS = {
    "easy": 150,
    "medium": 225,
    "hard": 125,
}


# ============================================================
# ADDITIONAL QUESTIONS FOR TOPIC QUOTAS
# ============================================================

def additional_quota_questions():
    questions = []
    seen = set()

    # --------------------------------------------------------
    # SLEEP
    # --------------------------------------------------------

    sleep_items = [
        (
            "Which habit can support better sleep consistency?",
            "Keeping a similar bedtime each night",
            ["Changing bedtime randomly", "Skipping sleep", "Using caffeine before bed"],
            "easy",
        ),
        (
            "Which factor can interfere with sleep for some people?",
            "Caffeine close to bedtime",
            ["A regular sleep schedule", "A dark room", "A consistent wake time"],
            "medium",
        ),
        (
            "Why can a dark sleeping environment be useful?",
            "It can support normal sleep conditions",
            ["It guarantees deep sleep", "It replaces recovery", "It eliminates fatigue"],
            "easy",
        ),
        (
            "Why can excessive screen exposure before bed be problematic for some people?",
            "It may make it harder to wind down",
            ["It guarantees sleep", "It replaces exercise", "It improves recovery automatically"],
            "medium",
        ),
        (
            "What is a useful sleep-related routine?",
            "Keeping a consistent wake time",
            ["Changing wake time every day", "Skipping sleep after exercise", "Using stimulants before bed"],
            "easy",
        ),
        (
            "Why is sleep relevant to physical performance?",
            "Poor sleep can affect alertness and performance",
            ["Sleep has no effect on performance", "Sleep replaces training", "Sleep replaces nutrition"],
            "medium",
        ),
        (
            "What can a person review when sleep quality remains poor?",
            "Sleep schedule and environmental factors",
            ["Only exercise intensity", "Only shoe choice", "Only music choice"],
            "medium",
        ),
        (
            "Which environment is generally more suitable for sleep?",
            "A quiet, dark, comfortable environment",
            ["A bright noisy room", "A constantly changing temperature", "A loud exercise area"],
            "easy",
        ),
        (
            "Why can a consistent wake time be useful?",
            "It can help maintain a regular sleep pattern",
            ["It guarantees perfect sleep", "It eliminates recovery", "It replaces exercise"],
            "easy",
        ),
        (
            "What should someone consider when increasing training while sleeping less?",
            "Whether recovery is still adequate",
            ["Adding even more training", "Removing rest days", "Ignoring fatigue"],
            "hard",
        ),
    ]

    for item in sleep_items:
        add_unique_question(
            questions,
            seen,
            item[0],
            [item[1]] + item[2],
            item[1],
            item[3],
            "sleep",
        )

    # Parameterized sleep questions
    for hours in range(4, 15):
        question = (
            f"An adult regularly sleeps about {hours} hours per night. "
            "Which statement best describes this duration?"
        )

        if hours < 7:
            correct = "The duration is below the general adult recommendation"
            difficulty = "easy"
        elif hours <= 9:
            correct = "The duration is within the general adult recommendation"
            difficulty = "easy"
        else:
            correct = "The duration is above the general adult recommendation"
            difficulty = "medium"

        add_unique_question(
            questions,
            seen,
            question,
            [
                correct,
                "The duration is always insufficient",
                "Sleep is unnecessary",
                "Recovery is unnecessary",
            ],
            correct,
            difficulty,
            "sleep",
        )

    # --------------------------------------------------------
    # HYDRATION
    # --------------------------------------------------------

    hydration_items = [
        (
            "What is one reason fluid needs can differ between people?",
            "Sweat rate and exercise conditions can differ",
            ["Everyone loses the same amount of fluid", "Temperature never matters", "Exercise duration never matters"],
            "medium",
        ),
        (
            "Which condition can increase sweat loss?",
            "Hot and humid conditions",
            ["Cold storage", "Quiet reading", "Sleeping"],
            "easy",),
        (
            "What should be considered after substantial sweating?",
            "Fluid replacement and electrolyte losses", ["Avoiding fluids", "Skipping recovery", "Avoiding all food"], "medium",),
        (
            "Why can exercise intensity affect hydration needs?","Higher intensity can increase sweat losses",
            ["Intensity eliminates sweating", "Intensity makes water unnecessary", "Intensity stops fluid loss"],
            "medium",),
        (
            "Which activity is most likely to require greater attention to fluid replacement?",
            "Long-duration exercise in heat", ["Reading for ten minutes", "Sleeping", "Sitting quietly"], "easy",),
        (
            "Why are electrolytes relevant during prolonged sweating?", "Sweat contains electrolytes that can be lost",
            ["Sweat contains no minerals", "Electrolytes replace all calories", "Electrolytes eliminate fatigue"],"medium",),
        (
            "What can increase the need to monitor hydration during exercise?",
            "Heavy sweating",["Low room noise", "Shoe color", "Playlist length"],"easy",),
        (
            "What is a sensible hydration strategy during exercise?","Adjust fluid intake to activity and environmental conditions",
            ["Avoid all fluids", "Ignore sweat losses", "Use exactly the same intake in every condition"],"medium",),
        (
            "Which environmental condition commonly increases attention to hydration?",
            "High temperature", ["Dark lighting", "Quiet music", "Cool storage"],"easy",),
        (
            "Why can humidity influence hydration considerations?",
            "It can affect sweat evaporation and heat loss", ["It eliminates sweating", "It prevents fluid loss", "It makes water unnecessary"],
            "hard",),
    ]
    for item in hydration_items:
        add_unique_question(questions,seen, item[0], [item[1]] + item[2],item[1], item[3], "hydration",)
    for minutes in range(20, 301, 10):
        if minutes <= 40:
            correct = "Maintain normal hydration habits and consider activity conditions"
            difficulty = "easy"
        elif minutes <= 90:
            correct = "Pay attention to fluid intake during the session"
            difficulty = "medium"
        else:
            correct = "Pay particular attention to fluid and electrolyte replacement"
            difficulty = "hard"
        question = (f"During a {minutes}-minute exercise session, "
            "which hydration consideration is appropriate?")
        add_unique_question(questions,seen,question,
            [correct,"Avoid all fluids", "Ignore sweat losses", "Remove all recovery periods",], correct, difficulty,"hydration",)

    general_items = [
        (
            "Which fitness component describes the ability to move a joint through a useful range?",
            "Flexibility or mobility", ["Maximum strength", "Hydration", "Sleep duration"], "easy",
        ),
        (
            "Which fitness component is most closely related to maintaining body control?", "Balance", ["Calorie intake", "Sleep duration", "Body temperature"], "easy",
        ),
        (
            "What does muscular endurance describe?", "The ability to perform repeated contractions over time", ["Maximum height", "Sleep duration", "Hydration"], "medium",
        ),
        (
            "What does body composition describe?", "The relative amounts of tissues and components in the body", ["Only height", "Only running speed", "Only flexibility"],
            "medium",
        ),
        (
            "Why can a balanced exercise program be useful?", "It can train different components of fitness", ["It eliminates recovery", "It guarantees identical results", "It replaces nutrition"], "medium",
        ),
        (
            "Why is consistency important in training?", "Adaptation generally requires repeated training over time", ["One workout determines everything", "Recovery is unnecessary", "Exercise has no cumulative effect"], "medium",
        ),
        (
            "What is muscular strength?","The ability to produce force",["The ability to sleep", "The ability to digest food", "The ability to stay hydrated"],
            "easy",
        ),
        (
            "What is cardiorespiratory endurance?", "The ability to sustain activity involving the heart and lungs",["Maximum flexibility", "Maximum grip strength", "Ability to balance without movement"],"medium",
        ),
        (
            "What is mobility generally concerned with?",
            "The ability to move through useful ranges of motion",["Only body weight", "Only calorie intake", "Only maximum force"],
            "easy",
        ),
        (
            "Why should a training plan include recovery?","Recovery helps manage training stress and supports adaptation",
            ["Recovery prevents all soreness", "Recovery replaces exercise", "Recovery makes nutrition unnecessary"],"medium",
        ),
    ]
    for item in general_items:
        add_unique_question(questions,seen,item[0],[item[1]] + item[2],item[1],item[3],"general",)
    for weight in range(45, 101, 5):
        for height_cm in range(150, 196, 5):
            height = height_cm / 100
            bmi = round(weight / (height * height), 1)
            question = (f"A person weighs {weight} kg and is "
                f"{height:.2f} m tall. What is the approximate BMI?")
            add_unique_question(questions,seen,question,
                [str(bmi), str(round(bmi + 1.5, 1)), str(round(max(1, bmi - 1.5), 1)), str(round(bmi + 3.0, 1)), ],
                str(bmi), "hard", "general",)
    return questions

def select_by_topic_and_difficulty(valid_questions,topic_targets, difficulty_targets,):
    """
    Select exactly the requested topic and difficulty totals.
    Selection is performed in two stages:
    1. Topic quotas are satisfied.
    2. Within each topic, the final set is adjusted to satisfy
       the global difficulty quotas.
    """
    by_topic = {}
    for question in valid_questions:
        topic = question["topic"]
        if topic not in topic_targets:
            continue
        by_topic.setdefault(topic, []).append(question)
    for topic, target in topic_targets.items():
        available = len(by_topic.get(topic, []))
        if available < target:
            raise RuntimeError(f"Not enough unique questions for topic '{topic}': "
                f"need {target}, available {available}.")
    for topic in by_topic:
        random.shuffle(by_topic[topic])
    selected = []
    for topic, target in topic_targets.items():
        selected.extend(by_topic[topic][:target])
    def difficulty_counts(items):
        counts = {"easy": 0,"medium": 0,"hard": 0,}
        for item in items:
            counts[item["difficulty"]] += 1
        return counts
    current = difficulty_counts(selected)
    print()
    print("Initial quota difficulty:")
    print(f"Easy   : {current['easy']}")
    print(f"Medium : {current['medium']}")
    print(f"Hard   : {current['hard']}")
    for topic, target in topic_targets.items():
        topic_selected = [
            item
            for item in selected
            if item["topic"] == topic]
        topic_pool = by_topic[topic]
        selected_ids = {normalize_text(item["question"])
            for item in topic_selected}
        max_attempts = 10000
        for _ in range(max_attempts):
            current = difficulty_counts(selected)
            if current == difficulty_targets:
                break
            over = [difficulty
                for difficulty in difficulty_targets
                if current[difficulty]
                > difficulty_targets[difficulty]]
            under = [
                difficulty
                for difficulty in difficulty_targets
                if current[difficulty]
                < difficulty_targets[difficulty]]
            if not over or not under:
                break
            changed = False
            for old_difficulty in over:
                for new_difficulty in under:
                    old_items = [
                        item
                        for item in topic_selected
                        if item["difficulty"] == old_difficulty]
                    replacement_candidates = [
                        item
                        for item in topic_pool
                        if item["difficulty"] == new_difficulty
                        and normalize_text(item["question"])
                        not in selected_ids]
                    if not old_items or not replacement_candidates:
                        continue
                    old_item = random.choice(old_items)
                    new_item = random.choice(replacement_candidates)
                    index = selected.index(old_item)
                    selected[index] = new_item
                    topic_selected.remove(old_item)
                    topic_selected.append(new_item)
                    selected_ids.remove(normalize_text(old_item["question"]))
                    selected_ids.add(normalize_text(new_item["question"]))
                    changed = True
                    break
                if changed:
                    break
            if not changed:
                break
    final_counts = difficulty_counts(selected)
    if final_counts != difficulty_targets:
        raise RuntimeError("Unable to satisfy exact difficulty quotas.\n"
            f"Required: {difficulty_targets}\n"
            f"Available after topic selection: {final_counts}")
    final_topic_counts = {}
    for item in selected:
        final_topic_counts[item["topic"]] = (final_topic_counts.get(item["topic"], 0) + 1)
    if final_topic_counts != topic_targets:
        raise RuntimeError("Unable to satisfy exact topic quotas.\n"
            f"Required: {topic_targets}\n"
            f"Generated: {final_topic_counts}")
    random.shuffle(selected)
    return selected

def generate_questions(total):
    if total != sum(TOPIC_TARGETS.values()):
        raise RuntimeError("TOTAL_QUESTIONS must equal the sum of TOPIC_TARGETS.")
    if total != sum(DIFFICULTY_TARGETS.values()):
        raise RuntimeError("TOTAL_QUESTIONS must equal the sum of DIFFICULTY_TARGETS.")
    generators = [("nutrition", nutrition_questions),("workout", workout_questions),
        ("recovery", recovery_questions),("sleep", sleep_questions),("hydration", hydration_questions),("general", general_questions),]
    all_questions = []
    print()
    print("Building offline question bank...")
    print()
    for topic, generator in generators:
        generated = generator()
        print(f"{topic.capitalize():12} | "
            f"available: {len(generated):4}")
        all_questions.extend(generated)
    extra = extra_parameterized_questions()
    print(f"{'Extra':12} | "
        f"available: {len(extra):4}")
    all_questions.extend(extra)
    quota_extra = additional_quota_questions()
    print(f"{'Quota extra':12} | "
        f"available: {len(quota_extra):4}")
    all_questions.extend(quota_extra)
    unique_questions = []
    seen_questions = set()
    for question in all_questions:
        key = normalize_text(question["question"])
        if key in seen_questions:
            continue
        seen_questions.add(key)
        unique_questions.append(question)
    valid_questions = [question
        for question in unique_questions
        if validate_question(question)]
    print()
    print("------------------------------------------")
    print(f"Total generated before deduplication : "
        f"{len(all_questions)}")
    print(f"Unique questions                     : "
        f"{len(unique_questions)}")
    print(f"Valid questions                      : "
        f"{len(valid_questions)}")
    print("------------------------------------------")
    available_topic_counts = {}
    for question in valid_questions:
        topic = question["topic"]
        if topic in TOPIC_TARGETS:
            available_topic_counts[topic] = (available_topic_counts.get(topic, 0) + 1)
    print()
    print("Available topic distribution:")
    print()
    for topic in TOPIC_TARGETS:
        print(f"{topic.capitalize():12} : "
            f"{available_topic_counts.get(topic, 0):4} "
            f"/ target {TOPIC_TARGETS[topic]:3}")
    available_difficulty_counts = {"easy": 0,"medium": 0,"hard": 0,}
    for question in valid_questions:
        difficulty = question["difficulty"]
        if difficulty in available_difficulty_counts:
            available_difficulty_counts[difficulty] += 1
    print()
    print("Available difficulty distribution:")
    print()
    for difficulty in DIFFICULTY_TARGETS:
        print(f"{difficulty.capitalize():12} : "
            f"{available_difficulty_counts[difficulty]:4} "
            f"/ target {DIFFICULTY_TARGETS[difficulty]:3}")
    selected = select_by_topic_and_difficulty(valid_questions,TOPIC_TARGETS,DIFFICULTY_TARGETS,)
    if len(selected) != total:
        raise RuntimeError(f"Expected {total} questions but selected "
            f"{len(selected)}.")
    return selected

def generate_questions(total):
    generators = [("nutrition", nutrition_questions, 100),("workout", workout_questions, 100),("recovery", recovery_questions, 80),("sleep", sleep_questions, 60),("hydration", hydration_questions, 60),("general", general_questions, 100),]
    all_questions = []
    print()
    print("Building offline question bank...")
    print()
    for topic, generator, target in generators:
        generated = generator()
        print(f"{topic.capitalize():12} | "
            f"available: {len(generated):3} | "
            f"target: {target:3}")
        all_questions.extend(generated)
    extra = extra_parameterized_questions()
    print(f"{'Extra':12} | "
        f"available: {len(extra):3}")
    all_questions.extend(extra)
    unique_questions = []
    seen_questions = set()
    for question in all_questions:
        key = normalize_text(question["question"])
        if key in seen_questions:
            continue
        seen_questions.add(key)
        unique_questions.append(question)
    valid_questions = [
        question
        for question in unique_questions
        if validate_question(question)
    ]
    print()
    print("------------------------------------------")
    print(f"Total generated before deduplication : "
        f"{len(all_questions)}")
    print(f"Unique questions                     : "
        f"{len(unique_questions)}")
    print(f"Valid questions                      : "
        f"{len(valid_questions)}")
    print("------------------------------------------")
    topic_counts = {}
    for question in valid_questions:
        topic = question["topic"]
        topic_counts[topic] = (topic_counts.get(topic, 0) + 1)
    print()
    print("Topic distribution:")
    print()
    for topic in sorted(topic_counts):
        print(f"{topic.capitalize():12} : "
            f"{topic_counts[topic]}")
    difficulty_counts = {}
    for question in valid_questions:
        difficulty = question["difficulty"]
        difficulty_counts[difficulty] = (difficulty_counts.get(difficulty, 0) + 1)
    print()
    print("Difficulty distribution:")
    print()
    for difficulty in ["easy","medium","hard",]:
        print(f"{difficulty.capitalize():12} : "
            f"{difficulty_counts.get(difficulty, 0)}")
    if len(valid_questions) < total:
        print()
        print(f"WARNING: Only {len(valid_questions)} "
            f"valid unique questions are available.")
        print(f"WARNING: Requested target was {total}.")
        print("WARNING: CSV will contain all valid questions.")
        selected = valid_questions
    else:
        random.shuffle(valid_questions)
        selected = valid_questions[:total]
    return selected

def save_csv(questions):
    fieldnames = ["question","option_a","option_b","option_c","option_d","correct_answer","difficulty","topic","source",]
    OUTPUT_FILE.parent.mkdir(parents=True, exist_ok=True,)
    with OUTPUT_FILE.open("w", encoding="utf-8-sig", newline="",) as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames,)
        writer.writeheader()
        for question in questions:
            writer.writerow(question)

def validate_csv_file():
    if not OUTPUT_FILE.exists():
        return
    with OUTPUT_FILE.open("r", encoding="utf-8-sig",newline="",) as file:
        reader = csv.DictReader(file)
        rows = list(reader)
    questions = [
        row["question"].strip()
        for row in rows
        if row.get("question")
    ]
    unique = len(
        {
            normalize_text(question)
            for question in questions
        }
    )
    invalid_answers = sum(
        row.get("correct_answer")
        not in {"A", "B", "C", "D"}
        for row in rows)
    invalid_options = 0
    for row in rows:
        options = [row.get("option_a", "").strip(),row.get("option_b", "").strip(),row.get("option_c", "").strip(), row.get("option_d", "").strip(),]
        if len(set(normalize_text(x) for x in options)) != 4:
            invalid_options += 1
    print()
    print("CSV validation:")
    print()
    print(f"CSV rows           : {len(rows)}")
    print(f"Unique questions   : {unique}")
    print(f"Invalid answers    : {invalid_answers}")
    print(f"Invalid option sets: {invalid_options}")
    print()

if __name__ == "__main__":
    print("Starting offline question generation...")
    print(f"Requested questions: {TOTAL_QUESTIONS}")
    questions = generate_questions(TOTAL_QUESTIONS)
    save_csv(questions)
    print()
    print("==========================================")
    print(" OFFLINE QUESTION GENERATION COMPLETE")
    print("==========================================")
    print(f"Questions generated : "f"{len(questions)}")
    print(f"CSV file            : "f"{OUTPUT_FILE}")
    print("==========================================")
    validate_csv_file()