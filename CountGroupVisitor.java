//visitor pattern, counts the total # of groups

public class CountGroupVisitor implements UserComponentVisitor {

    private int count = 0;


    public void visitUser(User user) {
        // Users are not groups
    }


    public void visitGroup(UserGroup group) {
       //only counts the groups that the user created 
        if (!group.getId().equals("Root")) {
            count++;
        }
    }

    public int getCount() { return count; }
}
