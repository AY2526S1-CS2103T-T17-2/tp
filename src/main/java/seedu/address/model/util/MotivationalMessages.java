package seedu.address.model.util;

import java.util.List;
import java.util.Random;


/**
 * Contains a list of motivational messages for students.
 */
public class MotivationalMessages {

    private static final List<String> MESSAGES = List.of(
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "The secret to getting ahead is getting started. - Mark Twain",
            "It’s not whether you get knocked down, it’s whether you get up. - Vince Lombardi",
            "The expert in anything was once a beginner.",
            "The beautiful thing about learning is that no one can take it away from you. - B.B. King",
            "Strive for progress, not perfection.",
            "Your limitation—it's only your imagination.",
            "Push yourself, because no one else is going to do it for you.",
            "Great things never come from comfort zones.",
            "Success doesn’t just find you. You have to go out and get it.",
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Don't watch the clock; do what it does. Keep going. - Sam Levenson",
            "The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt",
            "You are never too old to set another goal or to dream a new dream. - C.S. Lewis"
    );

    private static final Random RANDOM = new Random();

    /**
     * Returns a random motivational message.
     * @return A random motivational message.
     */
    public static String getRandomMessage() {
        return MESSAGES.get(RANDOM.nextInt(MESSAGES.size()));
    }
}
