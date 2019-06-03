package bot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class FramePlayerInfo extends JFrame {
  private static final long serialVersionUID = 1L;
  private JPanel container;
  private JLabel textBasicInfo;
  private JButton refresh;
  private JTextArea display;
  private PokemonGo go;
  private Bot bot;
  private JScrollPane scroll;

  public FramePlayerInfo(PokemonGo go) {
    this.go = go;
    this.bot = new Bot(go);
    setTitle("Player info");
    int width = 300;
    int height = 450;
    setSize(width, height);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);
    setDefaultCloseOperation(1);

    this.container = new JPanel(null);
    add(this.container);
    this.container.setBounds(0, 0, width, height);

    this.textBasicInfo = new JLabel("Info and Stats");
    this.container.add(this.textBasicInfo);
    this.textBasicInfo.setBounds(10, 10, 130, 20);

    this.display = new JTextArea();
    this.display.setEditable(false);
    this.scroll = new JScrollPane(this.display);
    this.scroll.setVerticalScrollBarPolicy(22);
    this.container.add(this.scroll);
    this.scroll.setBounds(10, 40, width - 20, height - 70);
    DefaultCaret caret = (DefaultCaret) this.display.getCaret();
    caret.setUpdatePolicy(1);
    this.display.setText(this.bot.printBasicInfo());
    this.display.setCaretPosition(0);

    this.refresh = new JButton("Refresh");
    this.container.add(this.refresh);
    this.refresh.setBounds(width - 110, 10, 100, 20);
    this.refresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FramePlayerInfo.this.refresh();
      }
    });
  }

  public void refresh() {
    try {
      this.go.getInventories().updateInventories();
    } catch (LoginFailedException e1) {
      e1.printStackTrace();
    } catch (RemoteServerException e1) {
      e1.printStackTrace();
    }
    this.display.setText(this.bot.printBasicInfo());
    this.display.setCaretPosition(0);
  }
}
