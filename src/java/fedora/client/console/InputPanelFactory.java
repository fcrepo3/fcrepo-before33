package fedora.client.console;

public abstract class InputPanelFactory {

    public static InputPanel getPanel(Class cl) {
        if (cl.getName().equals("java.lang.String")) {
            return new StringInputPanel();
        }
        if (cl.getName().equals("[B")) {
            return new ByteArrayInputPanel(true);
        }
        if (cl.getName().equals("boolean")) {
            return new BooleanInputPanel(true);
        }
        if (cl.getName().equals("java.lang.Boolean")) {
            return new BooleanInputPanel(false);
        }
        if (cl.getName().equals("java.util.Calendar")) {
          return new DateTimeInputPanel();
        }
        if (cl.getName().startsWith("[L")) {
            try {
                return new ArrayInputPanel(Class.forName(
                        cl.getName().substring(2, cl.getName().length()-1)));
            } catch (ClassNotFoundException cnfe) {
                // will fall through as unrecognized
            }
        }
        System.out.println("Unrecognized type: " + cl.getName());
        return NullInputPanel.getInstance();
    }

}