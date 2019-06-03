package bot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class FrameBot extends JFrame {
  private static final long serialVersionUID = 1L;
  private JPanel panelMain;
  private JPanel panelPlayer;
  private JPanel panelInfo;
  private JPanel panelCommands;
  private JPanel panelDisplay;
  private JLabel labelUserInfo;
  protected JButton buttonInventory;
  protected JButton buttonPokebank;
  private final int mButtonWidth = 100;
  protected JButton buttonStats;
  protected JButton buttonStart;
  protected JButton buttonStop;
  protected JButton buttonOptions;
  private JTextArea textAreaDisplay;
  private FramePlayerInfo frameInfo;
  private FramePokebank framePokebank;
  private FrameInventory frameInventory;
  private FrameOptions frameOptions;
  private PokemonGo go;
  protected HashMap<Integer, String[]> moveset;

  public FrameBot(final PokemonGo go, final double latitude, final double longitude) {
    this.go = go;
    go.setLatitude(latitude);
    go.setLongitude(longitude);
    go.setAltitude(0.0D);
    setTitle("jPokeBot");
    int width = 540;
    int height = 460;
    setSize(width, height);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);
    setDefaultCloseOperation(3);

    this.panelMain = new JPanel(null);
    add(this.panelMain);
    this.panelMain.setBounds(0, 0, width, height);

    this.panelDisplay = new JPanel(null);
    this.panelDisplay.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Display"));
    this.panelDisplay.setBounds(10, 130, width - 20, 300);

    this.textAreaDisplay = new JTextArea();
    this.textAreaDisplay.setEditable(false);
    JScrollPane scroll = new JScrollPane(this.textAreaDisplay);
    scroll.setVerticalScrollBarPolicy(22);
    this.panelDisplay.add(scroll);
    scroll.setBounds(10, 20, width - 40, 270);
    DefaultCaret caret = (DefaultCaret) this.textAreaDisplay.getCaret();
    caret.setUpdatePolicy(2);

    this.textAreaDisplay.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent me) {
      }
    });

    final Bot bot = new Bot(go, this.textAreaDisplay, this);

    this.panelPlayer = new JPanel(null);
    this.panelPlayer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Player"));
    this.panelPlayer.setBounds(10, 10, width / 2 - 80, 115);

    this.labelUserInfo = new JLabel("");
    updateUserInfo();
    this.panelPlayer.add(this.labelUserInfo);
    this.labelUserInfo.setBounds(10, 12, 200, 100);

    this.panelInfo = new JPanel(null);
    this.panelInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Info"));
    this.panelInfo.setBounds(width / 2 - 70, 10, width / 2 + 60, 55);

    this.buttonStats = new JButton("Stats");
    this.panelInfo.add(this.buttonStats);
    this.buttonStats.setBounds(10, 20, 100, 20);
    this.buttonStats.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FrameBot.this.frameInfo.refresh();
        FrameBot.this.frameInfo.setVisible(true);
      }
    });

    this.buttonPokebank = new JButton("Pokebank");
    this.panelInfo.add(this.buttonPokebank);
    this.buttonPokebank.setBounds(115, 20, 100, 20);
    this.buttonPokebank.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FrameBot.this.framePokebank.refresh();
        FrameBot.this.framePokebank.setVisible(true);
      }
    });

    this.buttonInventory = new JButton("Inventory");
    this.panelInfo.add(this.buttonInventory);
    this.buttonInventory.setBounds(220, 20, 100, 20);
    this.buttonInventory.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FrameBot.this.frameInventory.refresh();
        FrameBot.this.frameInventory.setVisible(true);
      }
    });

    this.panelCommands = new JPanel(null);
    this.panelCommands.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Bot commands"));
    this.panelCommands.setBounds(width / 2 - 70, 70, width / 2 + 60, 55);

    this.buttonStart = new JButton("Start");
    this.panelCommands.add(this.buttonStart);
    this.buttonStart.setBounds(10, 20, 100, 20);
    this.buttonStart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bot.startBot();
        go.setLatitude(latitude);
        go.setLongitude(longitude);
        FrameBot.this.buttonStop.setEnabled(true);
        FrameBot.this.buttonStart.setEnabled(false);
        FrameBot.this.buttonOptions.setEnabled(false);
        FrameBot.this.frameOptions.setVisible(false);
      }
    });

    this.buttonStop = new JButton("Stop");
    this.panelCommands.add(this.buttonStop);
    this.buttonStop.setEnabled(false);
    this.buttonStop.setBounds(115, 20, 100, 20);
    this.buttonStop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bot.stopBot();
        FrameBot.this.buttonStop.setEnabled(false);
        FrameBot.this.buttonStart.setEnabled(true);
        FrameBot.this.buttonOptions.setEnabled(true);
      }
    });

    this.buttonOptions = new JButton("Options");
    this.panelCommands.add(this.buttonOptions);
    this.buttonOptions.setBounds(220, 20, 100, 20);
    this.buttonOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FrameBot.this.frameOptions.setVisible(true);
      }
    });

    this.panelMain.add(this.panelPlayer);
    this.panelMain.add(this.panelInfo);
    this.panelMain.add(this.panelCommands);
    this.panelMain.add(this.panelDisplay);

    this.frameInfo = new FramePlayerInfo(go);
    this.framePokebank = new FramePokebank(go);
    this.frameInventory = new FrameInventory(go);
    this.frameOptions = new FrameOptions(go);

    loadSettingsGeneral();
    loadSettingsPokemonCatch();
    loadSettingsPokemonTransfer();
    loadSettingsPokemonRename();
    loadSettingsEggHatch();

    loadSettingsRecycle();
  }

  public void updateUserInfo() {
    try {
      this.go.getInventories().updateInventories();
      this.go.getPlayerProfile().updateProfile();
    } catch (LoginFailedException e) {
      e.printStackTrace();
    } catch (RemoteServerException e) {
      e.printStackTrace();
    }
    this.labelUserInfo.setText("<html><body>Name: " + this.go.getPlayerProfile().getPlayerData().getUsername()
        + "<br>Level: " + this.go.getPlayerProfile().getStats().getLevel() + "<br>XP next level: "
        + (this.go.getPlayerProfile().getStats().getNextLevelXp()
            - this.go.getPlayerProfile().getStats().getExperience())
        + "<br>Pokebank: " + this.go.getInventories().getPokebank().getPokemons().size() + "/"
        + this.go.getPlayerProfile().getPlayerData().getMaxPokemonStorage() + "<br>Inventory: "
        + this.go.getInventories().getItemBag().getItemsCount() + "/"
        + this.go.getPlayerProfile().getPlayerData().getMaxItemStorage() + "<br>Stardust: "
        + this.go.getPlayerProfile().getCurrency(PlayerProfile.Currency.STARDUST) + "</body></html>");
    repaint();
  }

  public void loadSettingsRecycle() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsItemRecycle.txt")));
    this.frameOptions.recycleType = "Balanced";
    boolean firstStart = false;
    try {
      String line;
      while ((line = br.readLine()) != null) {
        String[] info = line.split("=");
        if (info.length == 2 && !info[1].equals("")) {
          if (info[0].equals("active")) {
            if (info[1].equals("true")) {
              this.frameOptions.cActiveRecycle.setSelected(true);
              continue;
            }
            this.frameOptions.cActiveRecycle.setSelected(false);
            continue;
          }
          if (info[0].equals("defaultConfig")) {
            this.frameOptions.recycleType = info[1];
            continue;
          }
          if (info[0].equals("firstStart")) {
            if (info[1].equals("true")) {
              firstStart = true;
              this.frameOptions.chooseDefaultRecycle.setSelectedItem("Balanced");
              this.frameOptions.setDefaultRecycle(this.go);
            }
            continue;
          }
          if (info[0].equals("autoUpdate")) {
            if (info[1].equals("true")) {
              this.frameOptions.autoUpdate.setSelected(true);
              continue;
            }
            this.frameOptions.autoUpdate.setSelected(false);
            continue;
          }
          if (info[0].equals("recycleWhenFull")) {
            if (info[1].equals("true")) {
              this.frameOptions.cRecycleWhenFull.setSelected(true);
              continue;
            }
            this.frameOptions.cRecycleWhenFull.setSelected(false);
            continue;
          }
          if (info[0].equals("keepMasterball")) {
            if (info[1].equals("true")) {
              this.frameOptions.cKeepMasterball.setSelected(true);
              continue;
            }
            this.frameOptions.cKeepMasterball.setSelected(false);
            continue;
          }
          if (info[0].equals("keepIncense")) {
            if (info[1].equals("true")) {
              this.frameOptions.cKeepIncense.setSelected(true);
              continue;
            }
            this.frameOptions.cKeepIncense.setSelected(false);
            continue;
          }
          if (info[0].equals("keepTroyDisk")) {
            if (info[1].equals("true")) {
              this.frameOptions.cKeepTroyDisk.setSelected(true);
              continue;
            }
            this.frameOptions.cKeepTroyDisk.setSelected(false);
            continue;
          }
          if (info[0].equals("keepLuckyEgg")) {
            if (info[1].equals("true")) {
              this.frameOptions.cKeepLuckyEgg.setSelected(true);
              continue;
            }
            this.frameOptions.cKeepLuckyEgg.setSelected(false);
            continue;
          }
          if (info[0].equals("pokeball")) {
            this.frameOptions.fPokeball.setText(info[1]);
            continue;
          }
          if (info[0].equals("megaball")) {
            this.frameOptions.fMegaball.setText(info[1]);
            continue;
          }
          if (info[0].equals("ultraball")) {
            this.frameOptions.fUltraball.setText(info[1]);
            continue;
          }
          if (info[0].equals("masterball")) {
            if (info[1].equals("infinity")) {
              this.frameOptions.fMasterball.setText("?");
              continue;
            }
            this.frameOptions.fMasterball.setText(info[1]);
            continue;
          }
          if (info[0].equals("potion")) {
            this.frameOptions.fPotion.setText(info[1]);
            continue;
          }
          if (info[0].equals("superpotion")) {
            this.frameOptions.fSuperPotion.setText(info[1]);
            continue;
          }
          if (info[0].equals("hyperpotion")) {
            this.frameOptions.fHyperPotion.setText(info[1]);
            continue;
          }
          if (info[0].equals("maxpotion")) {
            this.frameOptions.fMaxPotion.setText(info[1]);
            continue;
          }
          if (info[0].equals("revive")) {
            this.frameOptions.fRevive.setText(info[1]);
            continue;
          }
          if (info[0].equals("maxrevive")) {
            this.frameOptions.fMaxRevive.setText(info[1]);
            continue;
          }
          if (info[0].equals("razzberry")) {
            this.frameOptions.fRazzberry.setText(info[1]);
            continue;
          }
          if (info[0].equals("incense")) {
            if (info[1].equals("infinity")) {
              this.frameOptions.fIncense.setText("?");
              continue;
            }
            this.frameOptions.fIncense.setText(info[1]);
            continue;
          }
          if (info[0].equals("troydisk")) {
            if (info[1].equals("infinity")) {
              this.frameOptions.fTroyDisk.setText("?");
              continue;
            }
            this.frameOptions.fTroyDisk.setText(info[1]);
            continue;
          }
          if (info[0].equals("luckyegg")) {
            if (info[1].equals("infinity")) {
              this.frameOptions.fLuckyEgg.setText("?");
              continue;
            }
            this.frameOptions.fLuckyEgg.setText(info[1]);
          }

        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.frameOptions.chooseDefaultRecycle.setSelectedItem(this.frameOptions.recycleType);
    if (!this.frameOptions.recycleType.equals("Custom")) {
      this.frameOptions.autoUpdate.setEnabled(true);
      this.frameOptions.setDefaultRecycle(this.go);
    }
    if (firstStart) {
      this.frameOptions.setDefaultRecycle(this.go);
      this.frameOptions.saveSettings();
    }
  }

  public void loadSettingsGeneral() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsGeneral.txt")));
    try {
      String line;
      while ((line = br.readLine()) != null) {
        String[] info = line.split("=");
        if (info.length == 2 && !info[1].equals("")) {
          if (info[0].equals("width")) {
            this.frameOptions.bWidth.setSelectedItem(info[1]);
            continue;
          }
          if (info[0].equals("timeToCollect")) {
            this.frameOptions.fTimeToCollect.setText(info[1]);
            continue;
          }
          if (info[0].equals("rescanTimeOut")) {
            this.frameOptions.fRescanTimeOut.setText(info[1]);
            continue;
          }
          if (info[0].equals("speed")) {
            this.frameOptions.fSpeed.setText(info[1]);
            continue;
          }
          if (info[0].equals("getLevelReward")) {
            if (info[1].equals("true")) {
              this.frameOptions.cGetLevelReward.setSelected(true);
              continue;
            }
            if (info[1].equals("trfalseue"))
              this.frameOptions.cGetLevelReward.setSelected(false);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadSettingsPokemonTransfer() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsTransfer.txt")));
    try {
      String line;
      while ((line = br.readLine()) != null) {
        String[] info = line.split("=");
        if (info.length == 2 && !info[1].equals("")) {
          if (info[0].equals("active")) {
            if (info[1].equals("true")) {
              this.frameOptions.cActiveTransfer.setSelected(true);
              this.frameOptions.enablePokemonTransferButtons();
              continue;
            }
            this.frameOptions.cActiveTransfer.setSelected(false);
            this.frameOptions.disablePokemonTransferButtons();
            continue;
          }
          if (info[0].equals("minCP")) {
            this.frameOptions.fMinCP.setText(info[1]);
            continue;
          }
          if (info[0].equals("minIV")) {
            this.frameOptions.fMinIV.setText(info[1]);
            continue;
          }
          if (info[0].equals("keepMinCP")) {
            if (info[1].equals("true")) {
              this.frameOptions.cMinCP.setSelected(true);
              this.frameOptions.fMinCP.setEnabled(true);
              continue;
            }
            this.frameOptions.cMinCP.setSelected(false);
            this.frameOptions.fMinCP.setEnabled(false);
            continue;
          }
          if (info[0].equals("keepMinIV")) {
            if (info[1].equals("true")) {
              this.frameOptions.cMinIV.setSelected(true);
              this.frameOptions.fMinIV.setEnabled(true);
              continue;
            }
            this.frameOptions.cMinIV.setSelected(false);
            this.frameOptions.fMinIV.setEnabled(false);
            continue;
          }
          if (info[0].equals("keepLegendaries")) {
            if (info[1].equals("true")) {
              this.frameOptions.cLegendary.setSelected(true);
              continue;
            }
            this.frameOptions.cLegendary.setSelected(false);
            continue;
          }
          if (info[0].equals("keepFavorites")) {
            if (info[1].equals("true")) {
              this.frameOptions.cFavorite.setSelected(true);
              continue;
            }
            this.frameOptions.cFavorite.setSelected(false);
            continue;
          }
          if (info[0].equals("keepPerfectMoves")) {
            if (info[1].equals("true")) {
              this.frameOptions.cPerfectMoves.setSelected(true);
              continue;
            }
            this.frameOptions.cPerfectMoves.setSelected(false);
            continue;
          }
          if (info[0].equals("activeSmartTransfer")) {
            if (info[1].equals("true")) {
              this.frameOptions.cActiveSmartTransfer.setSelected(true);
              if (this.frameOptions.prioritySlider.getValue() != 50)
                this.frameOptions.balancePriority.setEnabled(true);
              this.frameOptions.prioritySlider.setEnabled(true);
              this.frameOptions.fMinAverage.setEnabled(true);
              continue;
            }
            this.frameOptions.cActiveSmartTransfer.setSelected(false);
            this.frameOptions.balancePriority.setEnabled(false);
            this.frameOptions.prioritySlider.setEnabled(false);
            this.frameOptions.fMinAverage.setEnabled(false);
            continue;
          }
          if (info[0].equals("priority")) {
            this.frameOptions.prioritySlider.setValue(Integer.parseInt(info[1]));
            this.frameOptions.lPriority.setText("Set your priority: [IV " + this.frameOptions.prioritySlider.getValue()
                + "%] [CP " + (100 - this.frameOptions.prioritySlider.getValue()) + "%]");
            continue;
          }
          if (info[0].equals("minAverage")) {
            this.frameOptions.fMinAverage.setText(info[1]);
            continue;
          }
          if (info[0].equals("maxDuplicate"))
            this.frameOptions.fMaxDuplicate.setText(info[1]);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    loadMoveset();
  }

  public void loadSettingsPokemonCatch() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsPokemonCatch.txt")));
    try {
      String line;
      while ((line = br.readLine()) != null) {
        String[] info = line.split("=");
        if (info.length == 2 && !info[1].equals("")) {
          if (info[0].equals("active")) {
            if (info[1].equals("true")) {
              this.frameOptions.cActiveCatch.setSelected(true);
            } else {
              this.frameOptions.cActiveCatch.setSelected(false);
            }
          } else if (info[0].equals("timeOutCatch")) {
            this.frameOptions.fTimeOutCatch.setText(info[1]);
          }
          if (info[0].equals("useBerry"))
            if (info[1].equals("true")) {
              this.frameOptions.cUseBerry.setSelected(true);
            } else {
              this.frameOptions.cUseBerry.setSelected(false);
            }
          if (info[0].equals("useMasterballOnLegendary"))
            if (info[1].equals("true")) {
              this.frameOptions.cUseMasterballOnLegendary.setSelected(true);
            } else {
              this.frameOptions.cUseMasterballOnLegendary.setSelected(false);
            }
          if (info[0].equals("pokeballToUse")) {
            int index = 0;
            if (info[1].equals("smart")) {
              index = 0;
              this.frameOptions.bDefaultPokeball.setEnabled(false);
            } else if (info[1].equals("best")) {
              index = 1;
              this.frameOptions.bDefaultPokeball.setEnabled(false);
            } else if (info[1].equals("custom")) {
              index = 2;
              this.frameOptions.bDefaultPokeball.setEnabled(true);
            }
            this.frameOptions.bUsePokeball.setSelectedIndex(index);
          }
          if (info[0].equals("defaultPokeball")) {
            this.frameOptions.bDefaultPokeball.setSelectedItem(info[1]);
          }
          if (info[0].equals("limitBerryUsed"))
            if (info[1].equals("true")) {
              this.frameOptions.cLimitBerry.setSelected(true);
              this.frameOptions.fMaxBerry.setEnabled(true);
            } else {
              this.frameOptions.cLimitBerry.setSelected(false);
              this.frameOptions.fMaxBerry.setEnabled(false);
            }
          if (info[0].equals("maxBerryUsed"))
            this.frameOptions.fMaxBerry.setText(info[1]);
          if (info[0].equals("limitPokeballUsed"))
            if (info[1].equals("true")) {
              this.frameOptions.cLimitPokeball.setSelected(true);
              this.frameOptions.fMaxPokeball.setEnabled(true);
            } else {
              this.frameOptions.cLimitPokeball.setSelected(false);
              this.frameOptions.fMaxPokeball.setEnabled(false);
            }
          if (info[0].equals("maxPokeballUsed"))
            this.frameOptions.fMaxPokeball.setText(info[1]);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadSettingsEggHatch() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsEggHatch.txt")));
    try {
      String line;
      while ((line = br.readLine()) != null) {
        String[] info = line.split("=");
        if (info.length == 2 && !info[1].equals("")) {
          if (info[0].equals("active")) {
            if (info[1].equals("true")) {
              this.frameOptions.cActiveEggHatch.setSelected(true);
              this.frameOptions.enableEggHatchButtons();
              continue;
            }
            this.frameOptions.cActiveEggHatch.setSelected(false);
            this.frameOptions.disableEggHatchButtons();
            continue;
          }
          if (info[0].equals("hatchType")) {
            if (info[1].equals("all")) {
              this.frameOptions.rHatchAllOrderedBy.setSelected(true);
              this.frameOptions.bKm.setEnabled(false);
              continue;
            }
            this.frameOptions.rHatchOnlySelected.setSelected(true);
            this.frameOptions.bOrderBy.setEnabled(false);
            continue;
          }
          if (info[0].equals("hatchOnlySelected")) {
            this.frameOptions.bKm.setSelectedItem(info[1]);
            continue;
          }
          if (info[0].equals("hatchAllOrderedBy")) {
            this.frameOptions.bOrderBy.setSelectedItem(info[1]);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadSettingsPokemonRename() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsRename.txt")));
    try {
      String line;
      while ((line = br.readLine()) != null) {
        String[] info = line.split("=");
        if (info.length == 2 && !info[1].equals("")) {
          if (info[0].equals("active")) {
            if (info[1].equals("true")) {
              this.frameOptions.cActiveRename.setSelected(true);
              continue;
            }
            this.frameOptions.cActiveRename.setSelected(false);
            continue;
          }
          if (info[0].equals("renameIV")) {
            if (info[1].equals("true")) {
              this.frameOptions.cRenameIV.setSelected(true);
              continue;
            }
            this.frameOptions.cRenameIV.setSelected(false);
            continue;
          }
          if (info[0].equals("renamePerfectMoves")) {
            if (info[1].equals("true")) {
              this.frameOptions.cRenamePerfectMoves.setSelected(true);
              continue;
            }
            this.frameOptions.cRenamePerfectMoves.setSelected(false);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadMoveset() {
    this.moveset = new HashMap();
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/jpokebot/config/moveset.txt")));
    try {
      String line;
      while ((line = br.readLine()) != null) {
        String[] split = line.split("\\|");
        String[] moves = new String[2];
        moves[0] = String.valueOf(split[2].toUpperCase().replaceAll(" ", "_")) + "_FAST";
        moves[1] = split[3].toUpperCase().replaceAll(" ", "_");
        this.moveset.put(Integer.valueOf(Integer.parseInt(split[0])), moves);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void updateSettings() {
    this.frameOptions.setDefaultRecycle(this.go);
    this.frameOptions.saveSettings();
  }
}
