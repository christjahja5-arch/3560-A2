//visitor pattern that counts total # of users

public class CountUserVisitor implements UserComponentVisitor {

    private int count = 0;

    public void visitUser(User user) {
        count++;
    }

    public void visitGroup(UserGroup group) {
    }

    public int getCount() { return count; }
}
