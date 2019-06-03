package bot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

public class FramePokebank extends JFrame {
  private static final long serialVersionUID = 1L;
  private JPanel container;
  private JLabel textPokebank;
  private JLabel textSort;
  private JButton refresh;
  private JEditorPane display;
  private ButtonGroup sortByGroup;
  private JRadioButton cpButton;
  private JRadioButton ivButton;
  private JRadioButton nButton;
  private JRadioButton azButton;
  private Bot bot;
  private JScrollPane scroll;
  private PokemonGo go;

  public FramePokebank(PokemonGo go) {
    this.go = go;
    this.bot = new Bot(go);
    setTitle("Pokebank");
    int width = 700;
    int height = 600;
    setSize(width, height);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);
    setDefaultCloseOperation(1);

    this.container = new JPanel(null);
    add(this.container);
    this.container.setBounds(0, 0, width, height);

    this.textSort = new JLabel("Sort by:");
    this.container.add(this.textSort);
    this.textSort.setBounds(10, 10, 50, 20);

    this.textPokebank = new JLabel("Count: " + go.getInventories().getPokebank().getPokemons().size() + "/"
        + go.getPlayerProfile().getPlayerData().getMaxPokemonStorage());
    this.container.add(this.textPokebank);
    this.textPokebank.setBounds(10, 30, 150, 20);

    this.sortByGroup = new ButtonGroup();
    this.cpButton = new JRadioButton("CP", true);
    this.ivButton = new JRadioButton("IV", false);
    this.nButton = new JRadioButton("#", false);
    this.azButton = new JRadioButton("AZ", false);
    this.sortByGroup.add(this.cpButton);
    this.sortByGroup.add(this.ivButton);
    this.sortByGroup.add(this.nButton);
    this.sortByGroup.add(this.azButton);
    this.container.add(this.cpButton);
    this.container.add(this.ivButton);
    this.container.add(this.nButton);
    this.container.add(this.azButton);
    this.cpButton.setBounds(60, 10, 50, 20);
    this.ivButton.setBounds(115, 10, 50, 20);
    this.nButton.setBounds(170, 10, 50, 20);
    this.azButton.setBounds(215, 10, 50, 20);

    this.display = new JEditorPane();
    this.display.setEditable(false);
    this.display.setContentType("text/html");
    this.scroll = new JScrollPane(this.display);
    this.scroll.setVerticalScrollBarPolicy(22);
    this.container.add(this.scroll);
    this.scroll.setBounds(10, 60, width - 20, height - 90);

    this.refresh = new JButton("Refresh");
    this.container.add(this.refresh);
    this.refresh.setBounds(width - 110, 10, 100, 20);
    this.refresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FramePokebank.this.refresh();
      }
    });

    refresh();
  }

  public void refresh() {
    String sortBy;
    try {
      this.go.getInventories().updateInventories();
    } catch (LoginFailedException e1) {
      e1.printStackTrace();
    } catch (RemoteServerException e1) {
      e1.printStackTrace();
    }

    if (this.cpButton.isSelected()) {
      sortBy = "cp";
    } else if (this.ivButton.isSelected()) {
      sortBy = "iv";
    } else if (this.azButton.isSelected()) {
      sortBy = "az";
    } else if (this.nButton.isSelected()) {
      sortBy = "number";
    } else {
      sortBy = "default";
    }
    this.display.setText(this.bot.printPokebank(sortBy));
    this.textPokebank.setText("Count: " + this.go.getInventories().getPokebank().getPokemons().size() + "/"
        + this.go.getPlayerProfile().getPlayerData().getMaxPokemonStorage());
    this.display.setCaretPosition(0);
  }
}
