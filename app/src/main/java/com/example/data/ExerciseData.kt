package com.example.data

data class Exercise(
    val name: String,
    val category: String,
    val difficulty: String,
    val instructions: List<String>,
    val musclesWorked: String,
    val sets: Int,
    val reps: Int,
    val caloriesBurned: Int,
    val mistakes: List<String>,
    val animationType: String // "pushup", "squat", "situp", "pullup", "plank", "pike"
)

data class Stretch(
    val name: String,
    val durationSeconds: Int,
    val instructions: List<String>,
    val benefits: String,
    val musclesTargeted: String,
    val animationType: String // "neck", "shoulder", "back", "hamstring", "quad"
)

data class WorkoutPlan(
    val name: String,
    val durationDays: Int,
    val description: String,
    val difficulty: String,
    val targetGoal: String,
    val exercises: List<Pair<String, String>> // Exercise name, Reps description
)

object ExerciseData {
    val exercises = listOf(
        // Chest
        Exercise(
            name = "Push-ups",
            category = "Chest",
            difficulty = "Medium",
            instructions = listOf(
                "Place hands flat on the floor, slightly wider than shoulder-width.",
                "Keep your body in a straight line from head to heels.",
                "Lower your chest until it almost touches the floor, keeping elbows at a 45-degree angle.",
                "Push back up to the starting position."
            ),
            musclesWorked = "Chest, Anterior Deltoids, Triceps",
            sets = 4,
            reps = 25,
            caloriesBurned = 100,
            mistakes = listOf("Sagging hips (arch in the lower back)", "Elbows flaring too far outward", "Incomplete range of motion"),
            animationType = "pushup"
        ),
        Exercise(
            name = "Diamond Push-ups",
            category = "Chest",
            difficulty = "Hard",
            instructions = listOf(
                "Assume a pushup position but place your hands close together under your chest.",
                "Form a diamond shape with your thumbs and index fingers.",
                "Lower your chest to your hands while keeping your elbows tucked close to your body.",
                "Push yourself back up to the starting position."
            ),
            musclesWorked = "Triceps, Inner Chest, Shoulders",
            sets = 3,
            reps = 15,
            caloriesBurned = 80,
            mistakes = listOf("Flaring elbows out", "Losing core stiffness", "Diamond hands placed too far forward of chest"),
            animationType = "pushup"
        ),
        Exercise(
            name = "Archer Push-ups",
            category = "Chest",
            difficulty = "Extreme",
            instructions = listOf(
                "Place your hands much wider than a normal pushup, pointing outward.",
                "As you lower your body, slide over to one side, bending that elbow while keeping the opposite arm completely straight.",
                "Push back up and transition to the other side for the next repetition."
            ),
            musclesWorked = "Chest, Unilateral Strength, Core, Shoulders",
            sets = 3,
            reps = 10,
            caloriesBurned = 90,
            mistakes = listOf("Bending the straight arm", "Rotating hips away from the floor", "Uneven hip heights"),
            animationType = "pushup"
        ),
        // Back
        Exercise(
            name = "Pull-ups",
            category = "Back",
            difficulty = "Hard",
            instructions = listOf(
                "Hang from a bar with an overhand grip, hands slightly wider than shoulder-width.",
                "Pull your shoulder blades down and back, then pull your chest up towards the bar.",
                "Clear the bar with your chin, then lower yourself slowly to a dead hang."
            ),
            musclesWorked = "Lats, Rhomboids, Biceps, Core",
            sets = 4,
            reps = 10,
            caloriesBurned = 120,
            mistakes = listOf("Kicking legs (kipping) to generate momentum", "Not completing the full range (dead hang to chin over bar)", "Shoulders hunched forward"),
            animationType = "pullup"
        ),
        Exercise(
            name = "Australian Pull-ups",
            category = "Back",
            difficulty = "Easy",
            instructions = listOf(
                "Find a low bar at waist height. Hang underneath with heels on the floor.",
                "Keep your body completely straight from head to heels.",
                "Pull your chest up to the bar, engaging your upper back muscles.",
                "Slowly lower back down to straight arms."
            ),
            musclesWorked = "Upper Back, Rear Deltoids, Biceps",
            sets = 3,
            reps = 15,
            caloriesBurned = 60,
            mistakes = listOf("Sagging glutes or hips", "Jerking the body", "Under-contracting the shoulder blades"),
            animationType = "pullup"
        ),
        // Legs
        Exercise(
            name = "Squats",
            category = "Legs",
            difficulty = "Easy",
            instructions = listOf(
                "Stand with feet shoulder-width apart, toes pointing slightly outward.",
                "Lower your hips back and down as if sitting in a chair, keeping your chest up and knees over toes.",
                "Go down until thighs are parallel to the floor (or deeper).",
                "Drive through your heels to return to stand."
            ),
            musclesWorked = "Quadriceps, Glutes, Hamstrings, Calves",
            sets = 4,
            reps = 25,
            caloriesBurned = 90,
            mistakes = listOf("Knees caving inward", "Heels lifting off the floor", "Rounding the lower back"),
            animationType = "squat"
        ),
        Exercise(
            name = "Jump Squats",
            category = "Legs",
            difficulty = "Hard",
            instructions = listOf(
                "Perform a standard squat until thighs are parallel.",
                "Explode upward, jumping as high as possible into the air.",
                "Land softly, immediately absorbing the impact by bending knees back into the squat."
            ),
            musclesWorked = "Quadriceps, Glutes, Calves, Cardiovascular",
            sets = 3,
            reps = 15,
            caloriesBurned = 110,
            mistakes = listOf("Stiff landings (harsh on knees)", "Not squatting deep enough", "Leaning too far forward"),
            animationType = "squat"
        ),
        // Shoulders
        Exercise(
            name = "Pike Push-ups",
            category = "Shoulders",
            difficulty = "Medium",
            instructions = listOf(
                "Get into a pushup position, then walk your feet forward so your hips are raised high in an inverted 'V' shape.",
                "Look towards your feet. Keep legs as straight as possible.",
                "Lower your head forward between your hands to form a tripod shape.",
                "Push through your shoulders to return to the pike position."
            ),
            musclesWorked = "Deltoids, Upper Chest, Triceps",
            sets = 3,
            reps = 12,
            caloriesBurned = 80,
            mistakes = listOf("Flaring elbows outward", "Lowering straight down instead of forward (no tripod)", "Bending knees too much"),
            animationType = "pike"
        ),
        // Core
        Exercise(
            name = "Plank",
            category = "Core",
            difficulty = "Easy",
            instructions = listOf(
                "Place your forearms on the floor, elbows directly under your shoulders.",
                "Extend your legs behind you, toes tucked.",
                "Contract your core, glutes, and quads to hold your body in a straight line.",
                "Avoid arching or raising your hips."
            ),
            musclesWorked = "Rectus Abdominis, Transverse Abdominis, Lower Back",
            sets = 3,
            reps = 60, // 60 seconds
            caloriesBurned = 50,
            mistakes = listOf("Hips sagging down", "Hips piked too high", "Holding breath"),
            animationType = "plank"
        ),
        Exercise(
            name = "Sit-ups",
            category = "Core",
            difficulty = "Easy",
            instructions = listOf(
                "Lie on your back with knees bent and feet flat on the floor.",
                "Cross hands over chest or place fingertips lightly behind ears.",
                "Engage your abs and lift your torso up toward your knees.",
                "Slowly lower yourself back to the starting position."
            ),
            musclesWorked = "Abs, Hip Flexors, Obliques",
            sets = 4,
            reps = 25,
            caloriesBurned = 70,
            mistakes = listOf("Pulling on the neck with hands", "Rounding the back excessively", "Using momentum instead of core control"),
            animationType = "situp"
        )
    )

    val stretches = listOf(
        Stretch(
            name = "Morning Neck Flow",
            durationSeconds = 30,
            instructions = listOf(
                "Sit or stand tall, shoulders relaxed.",
                "Gently drop your right ear to your right shoulder, hold for 10 seconds.",
                "Roll your chin slowly to your chest, then to the left shoulder.",
                "Repeat slowly in a continuous rhythmic manner."
            ),
            benefits = "Alleviates sleep-induced neck stiffness, increases cervical range of motion.",
            musclesTargeted = "Trapezius, Scalenes, Sternocleidomastoid",
            animationType = "neck"
        ),
        Stretch(
            name = "Full Body Reach",
            durationSeconds = 45,
            instructions = listOf(
                "Stand with feet shoulder-width apart.",
                "Interlock your fingers and push your palms upwards to the ceiling.",
                "Reach as high as possible, rising up onto your toes.",
                "Inhale deeply, stretch the spine, and lean slightly left and right."
            ),
            benefits = "Decompresses the spine, stretches the abdominal wall, and improves morning alertness.",
            musclesTargeted = "Spine, Lats, Calves, Shoulders",
            animationType = "shoulder"
        ),
        Stretch(
            name = "Cobra Back Stretch",
            durationSeconds = 40,
            instructions = listOf(
                "Lie face down on the floor, palms flat under your shoulders.",
                "Slowly press through your hands to lift your chest off the floor.",
                "Keep your hips firmly grounded and shoulders rolled back.",
                "Look gently upwards and breathe."
            ),
            benefits = "Opens up the abdominal region, improves back flexibility, counters slouching.",
            musclesTargeted = "Lower Back, Core, Chest, Anterior Shoulders",
            animationType = "back"
        ),
        Stretch(
            name = "Hamstring Fold",
            durationSeconds = 45,
            instructions = listOf(
                "Stand tall, then fold forward from your hips.",
                "Let your head and arms hang heavy towards the floor.",
                "Keep knees slightly soft to protect the lower back.",
                "Try to touch your toes or let gravity draw your palms closer to the ground."
            ),
            benefits = "Releases posterior chain tension, relieves lower back strain, improves hamstring length.",
            musclesTargeted = "Hamstrings, Calves, Lower Back",
            animationType = "hamstring"
        ),
        Stretch(
            name = "Standing Quad Stretch",
            durationSeconds = 30,
            instructions = listOf(
                "Stand on one leg, holding a wall for balance if needed.",
                "Bend your other knee and grab your ankle behind your back.",
                "Gently pull your heel toward your glute while keeping knees aligned and pushing hips forward."
            ),
            benefits = "Lengthens the anterior thigh muscles, relieves knee tension.",
            musclesTargeted = "Quadriceps, Hip Flexors",
            animationType = "quad"
        )
    )

    val plans = listOf(
        WorkoutPlan(
            name = "30-Day Beginner",
            durationDays = 30,
            description = "The absolute safest launchpad. Gradually builds the joint integrity, endurance, and calisthenics foundation needed for higher intensity.",
            difficulty = "Easy",
            targetGoal = "Build basic joint strength and muscle habit.",
            exercises = listOf(
                "Push-ups" to "10 reps",
                "Sit-ups" to "10 reps",
                "Squats" to "10 reps",
                "Plank" to "30 seconds hold",
                "Morning Neck Flow" to "30 seconds stretch"
            )
        ),
        WorkoutPlan(
            name = "60-Day Strength",
            durationDays = 60,
            description = "An intermediate plan focused on raising muscular endurance and boosting pushup/squat volumes toward Saitama's levels.",
            difficulty = "Medium",
            targetGoal = "Intermediate muscular power and core stamina.",
            exercises = listOf(
                "Push-ups" to "40 reps",
                "Sit-ups" to "40 reps",
                "Squats" to "40 reps",
                "Pike Push-ups" to "10 reps",
                "Full Body Reach" to "45 seconds"
            )
        ),
        WorkoutPlan(
            name = "90-Day Saitama Challenge",
            durationDays = 90,
            description = "The legendary regimen. 100 Push-ups, 100 Sit-ups, 100 Squats, and a 10 km Run every day. Recommended for advanced athletes who have built high joint resilience.",
            difficulty = "Hard",
            targetGoal = "Legendary physical strength, endurance, and baldness-defying discipline.",
            exercises = listOf(
                "Push-ups" to "100 reps",
                "Sit-ups" to "100 reps",
                "Squats" to "100 reps",
                "10 km Run" to "Daily target"
            )
        ),
        WorkoutPlan(
            name = "Weight Loss & Run",
            durationDays = 30,
            description = "Combines high-volume lower-intensity bodyweight exercises with daily cardio to maximize caloric burn and shred body fat.",
            difficulty = "Medium",
            targetGoal = "High caloric deficit and lung capacity.",
            exercises = listOf(
                "Squats" to "30 reps",
                "Sit-ups" to "45 reps",
                "Plank" to "60 seconds hold",
                "Hamstring Fold" to "45 seconds stretch"
            )
        )
    )
}
