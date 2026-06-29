import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

//singleton pattern where there is only one admin control panel with global access
public class AdminControlPanel extends JFrame {


    private static AdminControlPanel instance;

    public static AdminControlPanel getInstance() {
        if (instance == null) {
            instance = new AdminControlPanel();
        }
        return instance;
    }

//private constructor 
    private AdminControlPanel() {
        super("Mini Twitter");
        // Composite pattern: Root is the implicit top-level group
        root = UserFactory.createGroup("Root");
        groupMap.put("Root", root);
        buildUI();
    }


    //root group
    private final UserGroup root;

    //stores the users and the groups
    private final Map<String, User>      userMap  = new LinkedHashMap<>();
    private final Map<String, UserGroup> groupMap = new LinkedHashMap<>();

    private DefaultMutableTreeNode rootTreeNode;
    private DefaultTreeModel       treeModel;
    private JTree                  tree;
    private JTextField userIdField;
    private JTextField groupIdField;

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(660, 540);
        setLayout(new BorderLayout(6, 6));

        //shows user/group tree
        rootTreeNode = new DefaultMutableTreeNode("[G] Root");
        treeModel    = new DefaultTreeModel(rootTreeNode);
        tree         = new JTree(treeModel);
        tree.setRootVisible(true);
        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setPreferredSize(new Dimension(210, 0));
        treeScroll.setBorder(BorderFactory.createTitledBorder("Users & Groups"));
        add(treeScroll, BorderLayout.WEST);

        //ui for the right controls
        JPanel right = new JPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.insets  = new Insets(4, 4, 4, 4);
        g.weightx = 1.0;

        int row = 0;

        //adds a user row
        row = addSectionLabel(right, g, row, "Add User:");
        userIdField = new JTextField();
        row = addFieldAndButton(right, g, row, userIdField, "Add User", e -> addUser());

        row = addSeparator(right, g, row);

        //adds group row
        row = addSectionLabel(right, g, row, "Add Group:");
        groupIdField = new JTextField();
        row = addFieldAndButton(right, g, row, groupIdField, "Add Group", e -> addGroup());

        row = addSeparator(right, g, row);

        //opens a window for the user
        row = addWideButton(right, g, row, "Open User View", e -> openUserView());

        //spacer
        g.gridy   = row++;
        g.weighty = 1.0;
        right.add(new JPanel(), g);
        g.weighty = 0;

        //data insights buttons
        row = addSectionLabel(right, g, row, "Data Insights:");
        row = addWideButton(right, g, row, "User Total",       e -> runAnalysis("users"));
        row = addWideButton(right, g, row, "Group Total",      e -> runAnalysis("groups"));
        row = addWideButton(right, g, row, "Messages Total",   e -> runAnalysis("messages"));
        row = addWideButton(right, g, row, "Positive %",       e -> runAnalysis("positive"));

        add(right, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private int addSectionLabel(JPanel p, GridBagConstraints g, int row, String text) {
        g.gridy = row; g.gridwidth = 2;
        p.add(new JLabel(text), g);
        return row + 1;
    }

    private int addFieldAndButton(JPanel p, GridBagConstraints g, int row,
                                  JTextField field, String btnText,
                                  java.awt.event.ActionListener action) {
        g.gridy    = row; g.gridx = 0; g.gridwidth = 1; g.weightx = 1.0;
        p.add(field, g);
        g.gridx    = 1; g.weightx = 0;
        JButton btn = new JButton(btnText);
        btn.addActionListener(action);
        p.add(btn, g);
        g.gridx = 0;
        return row + 1;
    }

    private int addSeparator(JPanel p, GridBagConstraints g, int row) {
        g.gridy = row; g.gridwidth = 2; g.weightx = 1.0;
        p.add(new JSeparator(), g);
        return row + 1;
    }

    private int addWideButton(JPanel p, GridBagConstraints g, int row,
                               String text, java.awt.event.ActionListener action) {
        g.gridy = row; g.gridwidth = 2; g.weightx = 1.0;
        JButton btn = new JButton(text);
        btn.addActionListener(action);
        p.add(btn, g);
        return row + 1;
    }

    //adds a new user

    private void addUser() {
        String id = userIdField.getText().trim();
        if (id.isEmpty()) { alert("Please enter a User ID."); return; }
        if (userMap.containsKey(id) || groupMap.containsKey(id)) {
            alert("ID already exists: " + id); return;
        }

        UserGroup target = getSelectedGroup();
        User user = UserFactory.createUser(id);
        userMap.put(id, user);
        target.add(user);

        //update tree
        DefaultMutableTreeNode parentNode = requireTreeNode(target.getId());
        DefaultMutableTreeNode userNode   = new DefaultMutableTreeNode(id);
        treeModel.insertNodeInto(userNode, parentNode, parentNode.getChildCount());
        tree.expandPath(new TreePath(parentNode.getPath()));
        tree.scrollPathToVisible(new TreePath(userNode.getPath()));
        userIdField.setText("");
    }

    //adds a new group

    private void addGroup() {
        String id = groupIdField.getText().trim();
        if (id.isEmpty()) { alert("Please enter a Group ID."); return; }
        if (groupMap.containsKey(id) || userMap.containsKey(id)) {
            alert("ID already exists: " + id); return;
        }

        UserGroup target   = getSelectedGroup();
        UserGroup newGroup = UserFactory.createGroup(id);
        groupMap.put(id, newGroup);
        target.add(newGroup);

        DefaultMutableTreeNode parentNode = requireTreeNode(target.getId());
        DefaultMutableTreeNode groupNode  = new DefaultMutableTreeNode("[G] " + id);
        treeModel.insertNodeInto(groupNode, parentNode, parentNode.getChildCount());
        tree.expandPath(new TreePath(parentNode.getPath()));
        tree.scrollPathToVisible(new TreePath(groupNode.getPath()));
        groupIdField.setText("");
    }

    //open user window
    private void openUserView() {
        DefaultMutableTreeNode selected = selectedNode();
        if (selected == null || isGroupNode(selected)) {
            alert("Please select a user node in the tree (not a group)."); return;
        }
        String userId = selected.getUserObject().toString();
        User user = userMap.get(userId);
        if (user == null) return;

        UserView view = new UserView(user, userMap);
        user.addView(view);
        view.setVisible(true);
    }

    //run insights
    private void runAnalysis(String type) {
        switch (type) {
            case "users": {
                CountUserVisitor v = new CountUserVisitor();
                root.accept(v); 
                alert("Total Users: " + v.getCount());
                break;
            }
            case "groups": {
                CountGroupVisitor v = new CountGroupVisitor();
                root.accept(v);
                alert("Total Groups: " + v.getCount());
                break;
            }
            case "messages": {
                CountTweetVisitor v = new CountTweetVisitor();
                root.accept(v);
                alert("Total Messages in All Feeds: " + v.getCount());
                break;
            }
            case "positive": {
                PositiveTweetVisitor v = new PositiveTweetVisitor();
                root.accept(v);
                alert(String.format("Positive Tweet Percentage: %.1f%%", v.getPercentage()));
                break;
            }
        }
    }

    //get the group being selected
    private UserGroup getSelectedGroup() {
        DefaultMutableTreeNode sel = selectedNode();
        if (sel != null) {
            if (isGroupNode(sel)) {
                UserGroup g = groupMap.get(groupIdFromLabel(sel));
                if (g != null) return g;
            } else {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) sel.getParent();
                if (parent != null) {
                    UserGroup g = groupMap.get(groupIdFromLabel(parent));
                    if (g != null) return g;
                }
            }
        }
        return root;
    }

    //get tree node
    private DefaultMutableTreeNode selectedNode() {
        return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    }

    //check if its a group
    private boolean isGroupNode(DefaultMutableTreeNode node) {
        return node.getUserObject().toString().startsWith("[G] ");
    }

    //get id for the group
    private String groupIdFromLabel(DefaultMutableTreeNode node) {
        String label = node.getUserObject().toString();
        return label.startsWith("[G] ") ? label.substring(4) : label;
    }

    //find tree node
    private DefaultMutableTreeNode requireTreeNode(String groupId) {
        DefaultMutableTreeNode result = findTreeNode(rootTreeNode, groupId);
        return result != null ? result : rootTreeNode;
    }

    //search the tree
    private DefaultMutableTreeNode findTreeNode(DefaultMutableTreeNode node, String targetId) {
        String label  = node.getUserObject().toString();
        String nodeId = label.startsWith("[G] ") ? label.substring(4) : label;
        if (nodeId.equals(targetId)) return node;
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode hit =
                findTreeNode((DefaultMutableTreeNode) node.getChildAt(i), targetId);
            if (hit != null) return hit;
        }
        return null;
    }


    private void alert(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public Map<String, User>      getUserMap()  { return userMap;  }
    public Map<String, UserGroup> getGroupMap() { return groupMap; }
    public UserGroup              getRoot()     { return root;     }
}
