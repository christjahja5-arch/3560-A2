import java.util.ArrayList;
import java.util.List;

    //composite function, users followers and following and tweets are stored
public class User implements UserComponent {

    private final String id;

    //observer pattern, user following 
    private final List<User> followers  = new ArrayList<>();

    //users following
    private final List<User> followings = new ArrayList<>();

    //tweets shown in the news feed
    private final List<String> newsFeed = new ArrayList<>();

    //opens the users windows
    private final List<UserView> views = new ArrayList<>();

    public User(String id) {
        this.id = id;
    }


 
    public String getId() { return id; }

    //aceepts the visitors


    public void accept(UserComponentVisitor visitor) {
        visitor.visitUser(this);
    }

    //follows another user
    public void follow(User target) {
        if (!followings.contains(target) && target != this) {
            followings.add(target);
            target.addFollower(this); 
        }
    }

//adds a follower
    public void addFollower(User follower) {
        if (!followers.contains(follower)) {
            followers.add(follower);
        }
    }

//oberserver pattern posting tweets 
    public void postTweet(String message) {
        String formatted = "[" + id + "]: " + message;
        newsFeed.add(formatted);   
        for (User follower : followers) {
            follower.receiveTweet(formatted); 
        }
        notifyViews();
    }

//recieve a tweet 
    public void receiveTweet(String message) {
        newsFeed.add(message);
        notifyViews(); 
    }

//adds user window
    public void addView(UserView view) {
        views.add(view);
    }

//refreshes feed
    private void notifyViews() {
        for (UserView view : views) {
            view.refreshNewsFeed();
        }
    }

//getters
    public List<User>   getFollowings() { return followings; }
    public List<String> getNewsFeed()   { return newsFeed;   }
}
