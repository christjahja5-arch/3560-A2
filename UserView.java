import javax.swing.*;
import java.awt.*;
import java.util.Map;

//oberserver pattern
//multiple window views can be open and closing a window wont exit the app completely
public class UserView extends JFrame {

    private final User              user;
    private final Map<String, User> userMap; //find user ID

    private final DefaultListModel<String> followingListModel = new DefaultListModel<>();
    private final DefaultListModel<String> newsFeedListModel  = new DefaultListModel<>();

    private JTextField followField;
    private JTextField tweetField;

    public UserView(User user, Map<String, User> userMap) {
        super("User View — " + user.getId());
        this.user    = user;
        this.userMap = userMap;
        buildUI();
        refreshFollowings();
        refreshNewsFeed();
    }

    //building the UI

    private void buildUI() {
        setSize(460, 500);
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //header
        JLabel header = new JLabel("  Viewing: " + user.getId());
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
        header.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        add(header, BorderLayout.NORTH);

        //center
        JPanel center = new JPanel(new GridLayout(1, 2, 5, 5));
        center.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        //follow list
        JList<String> followingList = new JList<>(followingListModel);
        JScrollPane   followScroll  = new JScrollPane(followingList);
        followScroll.setBorder(BorderFactory.createTitledBorder("Following"));
        center.add(followScroll);

        //user tweets in right list
        JList<String> feedList   = new JList<>(newsFeedListModel);
        JScrollPane   feedScroll = new JScrollPane(feedList);
        feedScroll.setBorder(BorderFactory.createTitledBorder("News Feed"));
        center.add(feedScroll);

        add(center, BorderLayout.CENTER);

        //follow and tweet UI
        JPanel bottom = new JPanel(new GridLayout(2, 3, 5, 5));
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        bottom.add(new JLabel("Follow User ID:"));
        followField = new JTextField();
        bottom.add(followField);
        JButton followBtn = new JButton("Follow User");
        followBtn.addActionListener(e -> followUser());
        bottom.add(followBtn);

        bottom.add(new JLabel("Tweet Message:"));
        tweetField = new JTextField();
        bottom.add(tweetField);
        JButton tweetBtn = new JButton("Post Tweet");
        tweetBtn.addActionListener(e -> postTweet());
        bottom.add(tweetBtn);

        add(bottom, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    //oberserver pattern, follows another user through their id and shows the following users tweets
    private void followUser() {
        String targetId = followField.getText().trim();
        if (targetId.isEmpty()) return;

        User target = userMap.get(targetId);
        if (target == null)  { alert("No user with ID: " + targetId); return; }
        if (target == user)  { alert("You cannot follow yourself.");   return; }

        user.follow(target);
        refreshFollowings();
        followField.setText("");
    }

    private void postTweet() {
        String msg = tweetField.getText().trim();
        if (msg.isEmpty()) return;
        user.postTweet(msg); 
        tweetField.setText("");
    }


    public void refreshNewsFeed() {
        SwingUtilities.invokeLater(() -> {
            newsFeedListModel.clear();
            for (String tweet : user.getNewsFeed()) {
                newsFeedListModel.addElement(tweet);
            }
        });
    }

    //refresh following list after a follow
    private void refreshFollowings() {
        followingListModel.clear();
        for (User following : user.getFollowings()) {
            followingListModel.addElement(following.getId());
        }
    }

    private void alert(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
