package fedora.swing.mdi;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.beans.*;

/**
 * <p><b>Title:</b> SortHeaderRenderer.java</p>
 * <p><b>Description:</b>
 * Menu component that handles the functionality expected of a standard
 * "Windows" menu for MDI applications.
 * <p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>Copyright &copy; 2002, 2003 by The Rector and Visitors of the University of
 * Virginia and Cornell University. All rights reserved.
 * Portions created by Gerald Nunn are Copyright &copy;
 * Gerald Nunn, originally made available at
 * http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi.html</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author Gerald Nunn, cwilper@cs.cornell.edu
 * @version $Id$
 */
public class WindowMenu extends JMenu {
    private MDIDesktopPane desktop;
    private JMenuItem cascade=new JMenuItem("Cascade");
    private JMenuItem tile=new JMenuItem("Tile");
    private JMenuItem minAll=new JMenuItem("Minimize All");
    private JMenuItem restoreAll=new JMenuItem("Restore All");

    public WindowMenu(MDIDesktopPane desktop, String name) {
        super(name);
        this.desktop=desktop;

        cascade.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.cascadeFrames();
            }
        });
        tile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.tileFrames();
            }
        });
        minAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.minimizeFrames();
            }
        });
        restoreAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.restoreFrames();
            }
        });
        addMenuListener(new MenuListener() {
            public void menuCanceled (MenuEvent e) {}

            public void menuDeselected (MenuEvent e) {
                removeAll();
            }

            public void menuSelected (MenuEvent e) {
                buildChildMenus();
            }
        });
    }

    /* Sets up the children menus depending on the current desktop state */
    private void buildChildMenus() {
        int i;
        JInternalFrame[] array = desktop.getAllFrames();

        add(cascade);
        add(tile);
        add(minAll);
        add(restoreAll);
        if (array.length > 0) addSeparator();
        cascade.setEnabled(array.length > 0);
        tile.setEnabled(array.length > 0);
        minAll.setEnabled(desktop.deIconifiedFrames()>0);
        restoreAll.setEnabled(desktop.iconifiedFrames()>0);

        ChildMenuItem menu;
        for (i = 0; i < array.length; i++) {
            menu = new ChildMenuItem(array[i]);
            menu.setState(i == 0);
            menu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JInternalFrame frame = ((ChildMenuItem)ae.getSource()).getFrame();
                    frame.moveToFront();
                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
                }
            });
            menu.setIcon(array[i].getFrameIcon());
            add(menu);
        }
    }

    /* This JCheckBoxMenuItem descendant is used to track the child frame that corresponds
       to a give menu. */
    class ChildMenuItem extends JCheckBoxMenuItem {
        private JInternalFrame frame;

        public ChildMenuItem(JInternalFrame frame) {
            super(frame.getTitle());
            this.frame=frame;
        }

        public JInternalFrame getFrame() {
            return frame;
        }
    }
}