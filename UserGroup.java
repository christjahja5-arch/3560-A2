import java.util.ArrayList;
import java.util.List;

//composite pattern allows for a group to be nested in each other
public class UserGroup implements UserComponent {

    private final String id;
    private final List<UserComponent> children = new ArrayList<>();

    public UserGroup(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public void add(UserComponent component) {
        children.add(component);
    }

    //visitor pattern 
    public void accept(UserComponentVisitor visitor) {
        visitor.visitGroup(this);
        for (UserComponent child : children) {
            child.accept(visitor);
        }
    }

    public List<UserComponent> getChildren() { return children; }
}