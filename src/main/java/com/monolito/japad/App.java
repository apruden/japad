package com.monolito.japad;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;

import javax.swing.SwingUtilities;

/**
 * 
 * 
 */
public class App {
    static MainFrame view = new MainFrame();
    static SketchModel model = new SketchModel();

    /**
     * 
     * @param args
     */
    public static void main(String... args) {
        createTables();

        @SuppressWarnings("unused")
        MainController controller = new MainController(view, model);

        StringBuilder sbHelp = new StringBuilder();
        sbHelp.append("\nUsage:\n");
        sbHelp.append("\tF5:\t\tcompile and run\n");
        sbHelp.append("\tCtrl + s:\tsave entry\n");
        sbHelp.append("\tCtrl + l:\tload entry\n");
        sbHelp.append("\n");
        sbHelp.append("dump() and show() used to dump variable or explore object in the Object explorer tree.\n\n");
        System.out.print(sbHelp.toString());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.setVisible(true);
            }
        });
    }

    /**
	 * 
	 */
    private static void createTables() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        String createString = "create table " + "japad.ENTRIES "
                + "(ID varchar(100) NOT NULL, " + "CODE long varchar, "
                + "PRIMARY KEY (ID))";

        String dbURL = "jdbc:derby:data/history;create=true";

        try (Connection conn = DriverManager.getConnection(dbURL);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createString);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }

    /**
     * 
     * @param obj
     */
    public static void show(Object obj) {
        model.addWatch(obj);

        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object value = null;

            if (obj != null) {
                try {
                    value = f.get(obj);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            System.out.format("%s %s: <%s> %s", f.getType(), f.getName(),
                    value.getClass(), value);
        }
    }

    /**
     * 
     * @param obj
     */
    public static void dump(Object... objs) {
        Arrays.asList(objs).forEach(obj -> 
            System.out.print(obj.toString())
        );
        System.out.println();
    }
}
