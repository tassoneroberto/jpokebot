package bot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FrameInventory extends JFrame {
  private static final long serialVersionUID = 1L;
  private JPanel container;
  private JLabel textInventory;
  private JButton refresh;
  private JEditorPane display;
  private JScrollPane scroll;
  private PokemonGo go;
  private Bot bot;

  public FrameInventory(PokemonGo go) {
    this.go = go;
    this.bot = new Bot(go);
    setTitle("Inventory");
    int width = 300;
    int height = 550;
    setSize(width, height);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);
    setDefaultCloseOperation(1);

    this.container = new JPanel(null);
    add(this.container);
    this.container.setBounds(0, 0, width, height);

    this.textInventory = new JLabel("Count: " + go.getInventories().getItemBag().getItemsCount() + "/"
        + go.getPlayerProfile().getPlayerData().getMaxItemStorage());
    this.container.add(this.textInventory);
    this.textInventory.setBounds(10, 10, 150, 20);

    this.display = new JEditorPane();
    this.display.setEditable(false);
    this.display.setContentType("text/html");
    this.scroll = new JScrollPane(this.display);
    this.scroll.setVerticalScrollBarPolicy(22);
    this.container.add(this.scroll);
    this.scroll.setBounds(10, 40, width - 20, height - 70);

    this.refresh = new JButton("Refresh");
    this.container.add(this.refresh);
    this.refresh.setBounds(width - 110, 10, 100, 20);
    this.refresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FrameInventory.this.refresh();
      }
    });

    refresh();
  }

  public void refresh() {
    try {
      this.go.getInventories().updateInventories();
    } catch (LoginFailedException e1) {
      e1.printStackTrace();
    } catch (RemoteServerException e1) {
      e1.printStackTrace();
    }
    this.display.setText(this.bot.printInventory());
    this.textInventory.setText("Count: " + this.go.getInventories().getItemBag().getItemsCount() + "/"
        + this.go.getPlayerProfile().getPlayerData().getMaxItemStorage());
    this.display.setCaretPosition(0);
  }
}
