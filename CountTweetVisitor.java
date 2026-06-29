//visitor pattern that counts all of the tweets in the news feed
public class CountTweetVisitor implements UserComponentVisitor {

    private int count = 0;


    public void visitUser(User user) {
        count += user.getNewsFeed().size();
    }


    public void visitGroup(UserGroup group) {
       //there isnt any tweets in groups
    }

    public int getCount() { return count; }
}
