import java.util.Arrays;
import java.util.List;

//visitor pattern, calculates the % of positive tweets in all news feed
public class PositiveTweetVisitor implements UserComponentVisitor {

    private int totalTweets    = 0;
    private int positiveTweets = 0;

    private static final List<String> POSITIVE_WORDS = Arrays.asList(
        "good", "great", "awesome", "fantastic", "amazing"
    );


    public void visitUser(User user) {
        for (String tweet : user.getNewsFeed()) {
            totalTweets++;
            String lower = tweet.toLowerCase();
            for (String word : POSITIVE_WORDS) {
                if (lower.contains(word)) {
                    positiveTweets++;
                    break; //count poisitive tweets only once
                }
            }
        }
    }


    public void visitGroup(UserGroup group) {
    }

    public double getPercentage() {
        if (totalTweets == 0) return 0.0;
        return (positiveTweets * 100.0) / totalTweets;
    }
}
