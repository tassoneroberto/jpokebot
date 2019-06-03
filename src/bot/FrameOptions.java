package bot;

import POGOProtos.Inventory.Item.ItemIdOuterClass;
import com.pokegoapi.api.PokemonGo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class FrameOptions extends JFrame {
  private static final long serialVersionUID = 1L;
  private PokemonGo go;
  private JPanel panelMain;
  private JPanel panelPokestop;
  private JPanel panelRename;
  private JPanel panelPokemonCatch;
  private JPanel panelRecycle;
  private JPanel panelTransfer;
  private JPanel panelEggHatch;
  private JTabbedPane tab;
  private JButton buttonSave;
  private JPanel customBag;
  protected JCheckBox cActiveRecycle;
  protected JCheckBox cRecycleWhenFull;
  protected JCheckBox cKeepIncense;
  protected JCheckBox cKeepMasterball;
  protected JCheckBox cKeepLuckyEgg;
  protected JCheckBox cKeepTroyDisk;
  private JLabel lPotion;
  private JLabel lSuperPotion;
  private JLabel lHyperPotion;
  private JLabel lMaxPotion;
  private JLabel lRevive;
  private JLabel lMaxRevive;
  private JLabel lUnlimitedBasicIncubator;
  private JLabel lPokeball;
  private JLabel lMegaball;
  private JLabel lUltraball;
  private JLabel lMasterball;
  private JLabel lLuckyEgg;
  private JLabel lIncense;
  private JLabel lTroyDisk;
  private JLabel lRazzberry;
  private JLabel lBasicIncubator;
  protected JTextField fPotion;
  protected JTextField fSuperPotion;
  protected JTextField fHyperPotion;
  protected JTextField fMaxPotion;
  protected JTextField fRevive;
  protected JTextField fMaxRevive;
  protected JTextField fUnlimitedBasicIncubator;
  protected JTextField fPokeball;
  protected JTextField fMegaball;
  protected JTextField fUltraball;
  protected JTextField fMasterball;
  protected JTextField fLuckyEgg;
  protected JTextField fIncense;
  protected JTextField fTroyDisk;
  protected JTextField fRazzberry;
  protected JTextField fBasicIncubator;
  private JLabel textCurrentConfig;
  private JTextField fCurrentConfig;
  private JLabel textMaxStorage;
  private JLabel textBagInputError;
  private JButton defaultRecycle;
  protected JComboBox<String> chooseDefaultRecycle;
  protected String recycleType;
  protected JCheckBox autoUpdate;
  protected JCheckBox cGetLevelReward;
  private JLabel lWidth;
  private JLabel lTimeToCollect;
  private JLabel lRescanTimeOut;
  private JLabel lSpeed;
  protected JTextField fTimeToCollect;
  protected JTextField fRescanTimeOut;
  protected JTextField fSpeed;
  protected JComboBox<String> bWidth;
  private JLabel textPokestopInputError;
  private JPanel smartTransfer;
  private JPanel panelPokeball;
  private JPanel panelBerry;
  protected JCheckBox cActiveCatch;
  protected JCheckBox cUseBerry;
  protected JCheckBox cUseMasterballOnLegendary;
  protected JCheckBox cLimitBerry;
  protected JCheckBox cLimitPokeball;
  protected JTextField fTimeOutCatch;
  protected JTextField fMaxBerry;
  protected JTextField fMaxPokeball;
  protected JComboBox<String> bUsePokeball;
  protected JComboBox<String> bDefaultPokeball;
  private JLabel lTimeOutCatch;
  private JLabel lMaxBerry;
  private JLabel lMaxPokeball;
  private JLabel textPokemonCatchInputError;
  protected JCheckBox cActiveTransfer;
  protected JCheckBox cMinCP;
  protected JCheckBox cMinIV;
  protected JCheckBox cFavorite;
  protected JCheckBox cPerfectMoves;
  protected JCheckBox cLegendary;
  protected JCheckBox cActiveSmartTransfer;
  protected JTextField fMinCP;
  protected JTextField fMinIV;
  protected JTextField fMinAverage;
  protected JTextField fMaxDuplicate;
  protected JButton balancePriority;
  protected JLabel lMaxDuplicate;
  protected JLabel lPriority;
  protected JLabel lMinAverage;
  protected JLabel lIV;
  protected JLabel lCP;
  protected JSlider prioritySlider;
  private JLabel textTransferInputError;
  protected JCheckBox cActiveEggHatch;
  protected ButtonGroup hatchType;
  protected JRadioButton rHatchOnlySelected;
  protected JRadioButton rHatchAllOrderedBy;
  protected JComboBox<String> bKm;
  protected JComboBox<String> bOrderBy;
  protected JCheckBox cActiveRename;
  protected JCheckBox cRenameIV;
  protected JCheckBox cRenamePerfectMoves;
  private final int textFieldWidth = 50;
  private final int textFieldHeight = 20;
  private final int labelWidth = 200;
  private final int labelHeight = 20;
  private boolean errorPokestop;
  private boolean errorRecycle;
  private boolean errorPokemonCatch;
  private boolean errorTransfer;
  private JLabel settingsError;

  public FrameOptions(PokemonGo go) {
  }

  protected void saveSettings() {
    saveGeneralSettings();
    savePokemonCatchSettings();
    savePokemonRenameSettings();
    savePokemonTransferSettings();
    saveRecycleItemsSettings();
    saveEggHatchSettings();
  }

  private void settingsError(String tab) {
    this.settingsError.setText("* Error in " + tab + " settings.");
    this.buttonSave.setEnabled(false);
  }

  private void checkTotalSettingsError() {
    if (!this.errorPokestop && !this.errorRecycle && !this.errorPokemonCatch && !this.errorTransfer) {
      this.settingsError.setText("");
      this.buttonSave.setEnabled(true);
    } else if (this.errorPokestop) {
      this.settingsError.setText("* Error in POKESTOP settings");
    } else if (this.errorRecycle) {
      this.settingsError.setText("* Error in ITEM RECYCLE settings");
    } else if (this.errorPokemonCatch) {
      this.settingsError.setText("* Error in POKEMON CATCH settings");
    } else if (this.errorTransfer) {
      this.settingsError.setText("* Error in TRANSFER settings");
    }
  }

  protected void checkPokestopInputData() {
    this.errorPokestop = false;
    try {
      int timeToCollect = Integer.parseInt(this.fTimeToCollect.getText());
      if (timeToCollect < 0)
        this.errorPokestop = true;
      int speed = Integer.parseInt(this.fTimeToCollect.getText());
      if (speed < 0)
        this.errorPokestop = true;
      int rescanTimeOut = Integer.parseInt(this.fRescanTimeOut.getText());
      if (rescanTimeOut < 0)
        this.errorPokestop = true;
    } catch (NumberFormatException e) {
      this.errorPokestop = true;
    }
    if (this.errorPokestop) {
      settingsError("POKESTOP");
      this.textPokestopInputError.setText("Only positive integer allowed!");
    } else {
      checkTotalSettingsError();
      this.textPokestopInputError.setText("");
    }
  }

  protected void checkPokemonCatchInputData() {
    this.errorPokemonCatch = false;
    try {
      int timeOutCatch = Integer.parseInt(this.fTimeOutCatch.getText());
      if (timeOutCatch < 0)
        this.errorPokemonCatch = true;
      int maxBerry = Integer.parseInt(this.fMaxBerry.getText());
      if (maxBerry < 0)
        this.errorPokemonCatch = true;
      int maxPokeball = Integer.parseInt(this.fMaxPokeball.getText());
      if (maxPokeball < 0)
        this.errorPokemonCatch = true;
    } catch (NumberFormatException e) {
      this.errorPokemonCatch = true;
    }
    if (this.errorPokemonCatch) {
      settingsError("POKEMON CATCH");
      this.textPokemonCatchInputError.setText("Only positive integer allowed!");
    } else {
      checkTotalSettingsError();
      this.textPokemonCatchInputError.setText("");
    }
  }

  protected void checkPokemonTransferInputData() {
    this.errorTransfer = false;
    boolean wrongInput = false;
    try {
      int maxDuplicate = Integer.parseInt(this.fMaxDuplicate.getText());
      if (maxDuplicate <= 0) {
        this.errorTransfer = true;
        this.textTransferInputError.setText("Max duplicate must be greater than 0!");
      }
      int minCP = Integer.parseInt(this.fMinCP.getText());
      if (minCP < 0) {
        this.errorTransfer = true;
        this.textTransferInputError.setText("Min CP must be positive!");
      }
      int minIV = Integer.parseInt(this.fMinIV.getText());
      if (minIV <= 0 || minIV > 100) {
        this.textTransferInputError.setText("IV must be included in 1-100!");
        this.errorTransfer = true;
      }
      int minAverage = Integer.parseInt(this.fMinAverage.getText());
      if (minAverage <= 0 || minAverage > 100) {
        this.textTransferInputError.setText("Min Average must be included in 1-100!");
        this.errorTransfer = true;
      }
    } catch (NumberFormatException e) {
      this.errorTransfer = true;
      wrongInput = true;
    }
    if (this.errorTransfer) {
      settingsError("POKEMON TRANSFER");
      if (wrongInput) {
        this.textTransferInputError.setText("Only positive integer allowed!");
      }
    } else {
      checkTotalSettingsError();
      this.textTransferInputError.setText("");
    }
  }

  private void saveGeneralSettings() {
    StringBuilder sb = new StringBuilder();
    sb.append("jPokeBot - GENERAL SETTINGS" + System.lineSeparator() + System.lineSeparator());
    sb.append("width=" + this.bWidth.getSelectedItem() + System.lineSeparator());
    sb.append("timeToCollect=" + this.fTimeToCollect.getText() + System.lineSeparator());
    sb.append("rescanTimeOut=" + this.fRescanTimeOut.getText() + System.lineSeparator());
    sb.append("speed=" + this.fSpeed.getText() + "\n");
    sb.append("getLevelReward=" + this.cGetLevelReward.isSelected());
    File settingsDataFile = new File("jpokebotuser/settingsGeneral.txt");

    try {
      FileWriter fw = new FileWriter(settingsDataFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void savePokemonCatchSettings() {
    StringBuilder sb = new StringBuilder();
    sb.append("jPokeBot - POKESTOP" + System.lineSeparator() + System.lineSeparator());
    sb.append("active=" + this.cActiveCatch.isSelected() + System.lineSeparator());
    sb.append("timeOutCatch=" + this.fTimeOutCatch.getText() + System.lineSeparator());
    sb.append("useBerry=" + this.cUseBerry.isSelected() + System.lineSeparator());
    sb.append("useMasterballOnLegendary=" + this.cUseMasterballOnLegendary.isSelected() + System.lineSeparator());
    String pokeballToUse = "smart";
    if (this.bUsePokeball.getSelectedIndex() == 0) {
      pokeballToUse = "smart";
    } else if (this.bUsePokeball.getSelectedIndex() == 1) {
      pokeballToUse = "best";
    } else if (this.bUsePokeball.getSelectedIndex() == 2) {
      pokeballToUse = "custom";
    }
    sb.append("pokeballToUse=" + pokeballToUse + System.lineSeparator());
    sb.append("defaultPokeball=" + this.bDefaultPokeball.getSelectedItem() + System.lineSeparator());
    sb.append("limitBerryUsed=" + this.cLimitBerry.isSelected() + System.lineSeparator());
    sb.append("maxBerryUsed=" + this.fMaxBerry.getText() + System.lineSeparator());
    sb.append("limitPokeballUsed=" + this.cLimitPokeball.isSelected() + System.lineSeparator());
    sb.append("maxPokeballUsed=" + this.fMaxPokeball.getText());

    File settingsDataFile = new File("jpokebotuser/settingsPokemonCatch.txt");

    try {
      FileWriter fw = new FileWriter(settingsDataFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void savePokemonTransferSettings() {
    StringBuilder sb = new StringBuilder();
    sb.append("jPokeBot - POKEMON TRANSFER" + System.lineSeparator() + System.lineSeparator());
    sb.append("active=" + this.cActiveTransfer.isSelected() + System.lineSeparator());
    sb.append("keepMinCP=" + this.cMinCP.isSelected() + System.lineSeparator());
    sb.append("keepMinIV=" + this.cMinIV.isSelected() + System.lineSeparator());
    sb.append("minCP=" + this.fMinCP.getText() + System.lineSeparator());
    sb.append("minIV=" + this.fMinIV.getText() + System.lineSeparator());
    sb.append("maxDuplicate=" + this.fMaxDuplicate.getText() + System.lineSeparator());
    sb.append("keepLegendaries=" + this.cLegendary.isSelected() + System.lineSeparator());
    sb.append("keepPerfectMoves=" + this.cPerfectMoves.isSelected() + System.lineSeparator());
    sb.append("keepFavorites=" + this.cFavorite.isSelected() + System.lineSeparator());
    sb.append("activeSmartTransfer=" + this.cActiveSmartTransfer.isSelected() + System.lineSeparator());
    sb.append("priority=" + this.prioritySlider.getValue() + System.lineSeparator());
    sb.append("minAverage=" + this.fMinAverage.getText());
    File settingsDataFile = new File("jpokebotuser/settingsTransfer.txt");

    try {
      FileWriter fw = new FileWriter(settingsDataFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void savePokemonRenameSettings() {
    StringBuilder sb = new StringBuilder();
    sb.append("jPokeBot - POKEMON RENAME" + System.lineSeparator() + System.lineSeparator());
    sb.append("active=" + this.cActiveRename.isSelected() + System.lineSeparator());
    sb.append("renameIV=" + this.cRenameIV.isSelected() + System.lineSeparator());
    sb.append("renamePerfectMoves=" + this.cRenamePerfectMoves.isSelected());

    File settingsDataFile = new File("jpokebotuser/settingsRename.txt");

    try {
      FileWriter fw = new FileWriter(settingsDataFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveEggHatchSettings() {
    StringBuilder sb = new StringBuilder();
    sb.append("jPokeBot - EGG HATCH" + System.lineSeparator() + System.lineSeparator());
    sb.append("active=" + this.cActiveEggHatch.isSelected() + System.lineSeparator());
    String hatchType = this.rHatchOnlySelected.isSelected() ? "selected" : "all";
    sb.append("hatchType=" + hatchType + System.lineSeparator());
    sb.append("hatchOnlySelected=" + this.bKm.getSelectedItem() + System.lineSeparator());
    sb.append("hatchAllOrderedBy=" + this.bOrderBy.getSelectedItem());

    File settingsDataFile = new File("jpokebotuser/settingsEggHatch.txt");

    try {
      FileWriter fw = new FileWriter(settingsDataFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveRecycleItemsSettings() {
    StringBuilder sb = new StringBuilder();
    sb.append("jPokeBot - ITEM RECYCLE SETTINGS" + System.lineSeparator() + System.lineSeparator());
    sb.append("active=");
    if (this.cActiveRecycle.isSelected()) {
      sb.append("true" + System.lineSeparator());
    } else {
      sb.append("false" + System.lineSeparator());
    }
    sb.append("recycleWhenFull=");
    if (this.cRecycleWhenFull.isSelected()) {
      sb.append("true" + System.lineSeparator());
    } else {
      sb.append("false" + System.lineSeparator());
    }
    sb.append("keepMasterball=");
    if (this.cKeepMasterball.isSelected()) {
      sb.append("true" + System.lineSeparator());
    } else {
      sb.append("false" + System.lineSeparator());
    }
    sb.append("keepIncense=");
    if (this.cKeepIncense.isSelected()) {
      sb.append("true" + System.lineSeparator());
    } else {
      sb.append("false" + System.lineSeparator());
    }
    sb.append("keepTroyDisk=");
    if (this.cKeepTroyDisk.isSelected()) {
      sb.append("true" + System.lineSeparator());
    } else {
      sb.append("false" + System.lineSeparator());
    }
    sb.append("keepLuckyEgg=");
    if (this.cKeepLuckyEgg.isSelected()) {
      sb.append("true" + System.lineSeparator());
    } else {
      sb.append("false" + System.lineSeparator());
    }
    sb.append("pokeball=" + this.fPokeball.getText() + System.lineSeparator());
    sb.append("megaball=" + this.fMegaball.getText() + System.lineSeparator());
    sb.append("ultraball=" + this.fUltraball.getText() + System.lineSeparator());
    sb.append("masterball=");
    if (this.cKeepMasterball.isSelected()) {
      sb.append("infinity" + System.lineSeparator());
    } else {
      sb.append(String.valueOf(this.fMasterball.getText()) + System.lineSeparator());
    }
    sb.append("potion=" + this.fPotion.getText() + System.lineSeparator());
    sb.append("superpotion=" + this.fSuperPotion.getText() + System.lineSeparator());
    sb.append("hyperpotion=" + this.fHyperPotion.getText() + System.lineSeparator());
    sb.append("maxpotion=" + this.fMaxPotion.getText() + System.lineSeparator());
    sb.append("revive=" + this.fRevive.getText() + System.lineSeparator());
    sb.append("maxrevive=" + this.fMaxRevive.getText() + System.lineSeparator());
    sb.append("razzberry=" + this.fRazzberry.getText() + System.lineSeparator());
    sb.append("luckyegg=");
    if (this.cKeepLuckyEgg.isSelected()) {
      sb.append("infinity" + System.lineSeparator());
    } else {
      sb.append(String.valueOf(this.fLuckyEgg.getText()) + System.lineSeparator());
    }
    sb.append("troydisk=");
    if (this.cKeepTroyDisk.isSelected()) {
      sb.append("infinity" + System.lineSeparator());
    } else {
      sb.append(String.valueOf(this.fTroyDisk.getText()) + System.lineSeparator());
    }
    sb.append("incense=");
    if (this.cKeepIncense.isSelected()) {
      sb.append("infinity" + System.lineSeparator());
    } else {
      sb.append(String.valueOf(this.fIncense.getText()) + System.lineSeparator());
    }
    sb.append(
        "defaultConfig=" + (String) this.chooseDefaultRecycle.getItemAt(this.chooseDefaultRecycle.getSelectedIndex())
            + System.lineSeparator());
    sb.append("autoUpdate=");
    if (this.autoUpdate.isSelected()) {
      sb.append("true" + System.lineSeparator());
    } else {
      sb.append("false" + System.lineSeparator());
    }
    sb.append("firstStart=false");

    File settingsDataFile = new File("jpokebotuser/settingsItemRecycle.txt");

    try {
      FileWriter fw = new FileWriter(settingsDataFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void setDefaultRecycle(PokemonGo go) {
    int maxFighterItems, maxCatcherItems;
    this.fMasterball.setText("?");
    this.fMasterball.setEnabled(false);
    this.cKeepMasterball.setSelected(true);

    this.fIncense.setText("?");
    this.fIncense.setEnabled(false);
    this.cKeepIncense.setSelected(true);

    this.fTroyDisk.setText("?");
    this.fTroyDisk.setEnabled(false);
    this.cKeepTroyDisk.setSelected(true);

    this.fLuckyEgg.setText("?");
    this.fLuckyEgg.setEnabled(false);
    this.cKeepLuckyEgg.setSelected(true);

    int level = go.getPlayerProfile().getStats().getLevel();
    int maxStorage = go.getPlayerProfile().getPlayerData().getMaxItemStorage()
        - go.getInventories().getItemBag().getItem(ItemIdOuterClass.ItemId.ITEM_INCUBATOR_BASIC).getCount()
        - go.getInventories().getItemBag().getItem(ItemIdOuterClass.ItemId.ITEM_INCUBATOR_BASIC_UNLIMITED).getCount();

    HashMap<String, Integer> config = new HashMap<String, Integer>();
    config.put("pokeball", Integer.valueOf(0));
    config.put("megaball", Integer.valueOf(0));
    config.put("ultraball", Integer.valueOf(0));
    config.put("masterball", Integer.valueOf(0));
    config.put("potion", Integer.valueOf(0));
    config.put("superpotion", Integer.valueOf(0));
    config.put("hyperpotion", Integer.valueOf(0));
    config.put("maxpotion", Integer.valueOf(0));
    config.put("revive", Integer.valueOf(0));
    config.put("maxrevive", Integer.valueOf(0));
    config.put("razzberry", Integer.valueOf(0));
    config.put("incense", Integer.valueOf(0));
    config.put("troydisk", Integer.valueOf(0));
    config.put("luckyegg", Integer.valueOf(0));
    if (this.recycleType.equals("Balanced")) {
      maxCatcherItems = maxStorage / 2;
      maxFighterItems = maxStorage / 2;
    } else if (this.recycleType.equals("Pokemon Catcher")) {
      maxCatcherItems = maxStorage * 4 / 5;
      maxFighterItems = maxStorage * 1 / 5;
    } else if (this.recycleType.equals("Gym Fighter")) {
      maxFighterItems = maxStorage * 4 / 5;
      maxCatcherItems = maxStorage * 1 / 5;
    } else if (this.recycleType.equals("Custom")) {
      maxFighterItems = 0;
      maxCatcherItems = 0;
      maxStorage = 0;
    } else {
      maxCatcherItems = maxStorage / 2;
      maxFighterItems = maxStorage / 2;
    }

    if (level < 5) {
      config.put("pokeball", Integer.valueOf(maxStorage));
    } else if (level < 8) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems));
      config.put("potion", Integer.valueOf(maxFighterItems / 2));
      config.put("revive", Integer.valueOf(maxFighterItems / 2));
    } else if (level < 10) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems * 4 / 5));
      config.put("razzberry", Integer.valueOf(maxCatcherItems * 1 / 5));
      config.put("potion", Integer.valueOf(maxFighterItems / 2));
      config.put("revive", Integer.valueOf(maxFighterItems / 2));
    } else if (level < 12) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems * 4 / 5));
      config.put("razzberry", Integer.valueOf(maxCatcherItems * 1 / 5));
      config.put("potion", Integer.valueOf(maxFighterItems / 2 * 1 / 4));
      config.put("superpotion", Integer.valueOf(maxFighterItems / 2 * 3 / 4));
      config.put("revive", Integer.valueOf(maxFighterItems / 2));
    } else if (level < 15) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems * 4 / 5 * 1 / 4));
      config.put("megaball", Integer.valueOf(maxCatcherItems * 4 / 5 * 3 / 4));
      config.put("razzberry", Integer.valueOf(maxCatcherItems * 1 / 5));
      config.put("potion", Integer.valueOf(maxFighterItems / 2 * 1 / 4));
      config.put("superpotion", Integer.valueOf(maxFighterItems / 2 * 3 / 4));
      config.put("revive", Integer.valueOf(maxFighterItems / 2));
    } else if (level < 20) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems * 4 / 5 * 1 / 4));
      config.put("megaball", Integer.valueOf(maxCatcherItems * 4 / 5 * 3 / 4));
      config.put("razzberry", Integer.valueOf(maxCatcherItems * 1 / 5));
      config.put("potion", Integer.valueOf(maxFighterItems / 2 * 1 / 10));
      config.put("superpotion", Integer.valueOf(maxFighterItems / 2 * 3 / 10));
      config.put("hyperpotion", Integer.valueOf(maxFighterItems / 2 * 6 / 10));
      config.put("revive", Integer.valueOf(maxFighterItems / 2));
    } else if (level < 25) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems * 4 / 5 * 1 / 10));
      config.put("megaball", Integer.valueOf(maxCatcherItems * 4 / 5 * 3 / 10));
      config.put("ultraball", Integer.valueOf(maxCatcherItems * 4 / 5 * 6 / 10));
      config.put("razzberry", Integer.valueOf(maxCatcherItems * 1 / 5));
      config.put("potion", Integer.valueOf(maxFighterItems / 2 * 1 / 10));
      config.put("superpotion", Integer.valueOf(maxFighterItems / 2 * 3 / 10));
      config.put("hyperpotion", Integer.valueOf(maxFighterItems / 2 * 6 / 10));
      config.put("revive", Integer.valueOf(maxFighterItems / 2));
    } else if (level < 30) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems * 4 / 5 * 1 / 10));
      config.put("megaball", Integer.valueOf(maxCatcherItems * 4 / 5 * 3 / 10));
      config.put("ultraball", Integer.valueOf(maxCatcherItems * 4 / 5 * 6 / 10));
      config.put("razzberry", Integer.valueOf(maxCatcherItems * 1 / 5));
      config.put("potion", Integer.valueOf(0));
      config.put("superpotion", Integer.valueOf(maxFighterItems / 2 * 1 / 10));
      config.put("hyperpotion", Integer.valueOf(maxFighterItems / 2 * 3 / 10));
      config.put("maxpotion", Integer.valueOf(maxFighterItems / 2 * 6 / 10));
      config.put("revive", Integer.valueOf(maxFighterItems / 2));
    } else if (level >= 30) {
      config.put("pokeball", Integer.valueOf(maxCatcherItems * 4 / 5 * 1 / 10));
      config.put("megaball", Integer.valueOf(maxCatcherItems * 4 / 5 * 3 / 10));
      config.put("ultraball", Integer.valueOf(maxCatcherItems * 4 / 5 * 6 / 10));
      config.put("razzberry", Integer.valueOf(maxCatcherItems * 1 / 5));
      config.put("potion", Integer.valueOf(0));
      config.put("superpotion", Integer.valueOf(maxFighterItems / 2 * 1 / 10));
      config.put("hyperpotion", Integer.valueOf(maxFighterItems / 2 * 3 / 10));
      config.put("maxpotion", Integer.valueOf(maxFighterItems / 2 * 6 / 10));
      config.put("revive", Integer.valueOf(maxFighterItems / 2 * 1 / 5));
      config.put("maxrevive", Integer.valueOf(maxFighterItems / 2 * 4 / 5));
    }

    this.fPokeball.setText(config.get("pokeball") + "");
    this.fMegaball.setText(config.get("megaball") + "");
    this.fUltraball.setText(config.get("ultraball") + "");
    this.fRazzberry.setText(config.get("razzberry") + "");
    this.fPotion.setText(config.get("potion") + "");
    this.fSuperPotion.setText(config.get("superpotion") + "");
    this.fHyperPotion.setText(config.get("hyperpotion") + "");
    this.fMaxPotion.setText(config.get("maxpotion") + "");
    this.fRevive.setText(config.get("revive") + "");
    this.fMaxRevive.setText(config.get("maxrevive") + "");

    this.chooseDefaultRecycle.setSelectedItem(this.recycleType);

    if (this.recycleType.equals("Custom")) {
      this.autoUpdate.setSelected(false);
      this.autoUpdate.setEnabled(false);
    } else {
      this.autoUpdate.setSelected(true);
      this.autoUpdate.setEnabled(true);
    }
  }

  private void changeToCustom() {
    refreshItemsCount();
    this.chooseDefaultRecycle.setSelectedItem("Custom");
    this.autoUpdate.setSelected(false);
    this.autoUpdate.setEnabled(false);
  }

  protected void refreshItemsCount() {
  }

  protected void enablePokemonCatchButtons() {
    this.fTimeOutCatch.setEnabled(true);
    this.cUseMasterballOnLegendary.setEnabled(true);
    this.bUsePokeball.setEnabled(true);
    if (this.bUsePokeball.getSelectedIndex() == 2) {
      this.bDefaultPokeball.setEnabled(true);
    } else {
      this.bDefaultPokeball.setEnabled(false);
    }
    this.cLimitPokeball.setEnabled(true);
    if (this.cLimitPokeball.isSelected()) {
      this.fMaxPokeball.setEnabled(true);
    } else {
      this.fMaxPokeball.setEnabled(false);
    }
    this.cUseBerry.setEnabled(true);
    if (this.cUseBerry.isSelected()) {
      this.cLimitBerry.setEnabled(true);
      if (this.cLimitBerry.isSelected()) {
        this.fMaxBerry.setEnabled(true);
      }
    } else {
      this.cLimitBerry.setEnabled(false);
      this.fMaxBerry.setEnabled(false);
    }
  }

  protected void disablePokemonCatchButtons() {
    this.fTimeOutCatch.setEnabled(false);
    this.cUseMasterballOnLegendary.setEnabled(false);
    this.bUsePokeball.setEnabled(false);
    this.bDefaultPokeball.setEnabled(false);
    this.cLimitPokeball.setEnabled(false);
    this.fMaxPokeball.setEnabled(false);
    this.cUseBerry.setEnabled(false);
    this.cLimitBerry.setEnabled(false);
    this.fMaxBerry.setEnabled(false);
  }

  protected void enablePokemonTransferButtons() {
    this.cLegendary.setEnabled(true);
    this.cFavorite.setEnabled(true);
    this.cPerfectMoves.setEnabled(true);
    this.cMinCP.setEnabled(true);
    this.cMinIV.setEnabled(true);
    this.fMaxDuplicate.setEnabled(true);
    this.cActiveSmartTransfer.setEnabled(true);
    if (this.cMinCP.isSelected()) {
      this.fMinCP.setEnabled(true);
    } else {
      this.fMinCP.setEnabled(false);
    }
    if (this.cMinIV.isSelected()) {
      this.fMinIV.setEnabled(true);
    } else {
      this.fMinIV.setEnabled(false);
    }
    if (this.cActiveSmartTransfer.isSelected()) {
      if (this.prioritySlider.getValue() != 50)
        this.balancePriority.setEnabled(true);
      this.prioritySlider.setEnabled(true);
      this.fMinAverage.setEnabled(true);
    } else {
      this.balancePriority.setEnabled(false);
      this.prioritySlider.setEnabled(false);
      this.fMinAverage.setEnabled(false);
    }
  }

  protected void disablePokemonTransferButtons() {
    this.cMinCP.setEnabled(false);
    this.cMinIV.setEnabled(false);
    this.fMinCP.setEnabled(false);
    this.fMinIV.setEnabled(false);
    this.fMaxDuplicate.setEnabled(false);
    this.cLegendary.setEnabled(false);
    this.cFavorite.setEnabled(false);
    this.cPerfectMoves.setEnabled(false);
    this.cActiveSmartTransfer.setEnabled(false);
    this.balancePriority.setEnabled(false);
    this.prioritySlider.setEnabled(false);
    this.fMinAverage.setEnabled(false);
  }

  protected void enablePokemonRenameButtons() {
    this.cRenameIV.setEnabled(true);
    this.cRenamePerfectMoves.setEnabled(true);
  }

  protected void disablePokemonRenameButtons() {
    this.cRenameIV.setEnabled(false);
    this.cRenamePerfectMoves.setEnabled(false);
  }

  protected void enableEggHatchButtons() {
    this.rHatchOnlySelected.setEnabled(true);
    if (this.rHatchOnlySelected.isSelected())
      this.bKm.setEnabled(true);
    this.rHatchAllOrderedBy.setEnabled(true);
    if (this.rHatchAllOrderedBy.isSelected())
      this.bOrderBy.setEnabled(true);
  }

  protected void disableEggHatchButtons() {
    this.rHatchOnlySelected.setEnabled(false);
    this.bKm.setEnabled(false);
    this.rHatchAllOrderedBy.setEnabled(false);
    this.bOrderBy.setEnabled(false);
  }

  protected void enableItemRecycleButtons() {
    this.cRecycleWhenFull.setEnabled(true);
    this.cKeepIncense.setEnabled(true);
    this.cKeepMasterball.setEnabled(true);
    this.cKeepLuckyEgg.setEnabled(true);
    this.cKeepTroyDisk.setEnabled(true);
    this.fPotion.setEnabled(true);
    this.fSuperPotion.setEnabled(true);
    this.fHyperPotion.setEnabled(true);
    this.fMaxPotion.setEnabled(true);
    this.fRevive.setEnabled(true);
    this.fMaxRevive.setEnabled(true);
    this.fPokeball.setEnabled(true);
    this.fMegaball.setEnabled(true);
    this.fUltraball.setEnabled(true);
    this.fRazzberry.setEnabled(true);
    this.defaultRecycle.setEnabled(true);
    this.chooseDefaultRecycle.setEnabled(true);
    this.fCurrentConfig.setEnabled(true);
    this.autoUpdate.setEnabled(true);
    if (this.cKeepMasterball.isSelected()) {
      this.fMasterball.setEnabled(false);
      this.fMasterball.setText("?");
    } else {
      this.fMasterball.setEnabled(true);
      this.fMasterball.setText("0");
    }
    if (this.cKeepLuckyEgg.isSelected()) {
      this.fLuckyEgg.setEnabled(false);
      this.fLuckyEgg.setText("?");
    } else {
      this.fLuckyEgg.setEnabled(true);
      this.fLuckyEgg.setText("0");
    }
    if (this.cKeepIncense.isSelected()) {
      this.fIncense.setEnabled(false);
      this.fIncense.setText("?");
    } else {
      this.fIncense.setEnabled(true);
      this.fIncense.setText("0");
    }
    if (this.cKeepTroyDisk.isSelected()) {
      this.fTroyDisk.setEnabled(false);
      this.fTroyDisk.setText("?");
    } else {
      this.fTroyDisk.setEnabled(true);
      this.fTroyDisk.setText("0");
    }
  }

  protected void disableItemRecycleButtons() {
    this.cRecycleWhenFull.setEnabled(false);
    this.cKeepIncense.setEnabled(false);
    this.cKeepMasterball.setEnabled(false);
    this.cKeepLuckyEgg.setEnabled(false);
    this.cKeepTroyDisk.setEnabled(false);
    this.fPotion.setEnabled(false);
    this.fSuperPotion.setEnabled(false);
    this.fHyperPotion.setEnabled(false);
    this.fMaxPotion.setEnabled(false);
    this.fRevive.setEnabled(false);
    this.fMaxRevive.setEnabled(false);
    this.fPokeball.setEnabled(false);
    this.fMegaball.setEnabled(false);
    this.fUltraball.setEnabled(false);
    this.fMasterball.setEnabled(false);
    this.defaultRecycle.setEnabled(false);
    this.chooseDefaultRecycle.setEnabled(false);
    this.fMasterball.setText("0");
    this.fLuckyEgg.setEnabled(false);
    this.fLuckyEgg.setText("0");
    this.fIncense.setEnabled(false);
    this.fIncense.setText("0");
    this.fTroyDisk.setEnabled(false);
    this.fTroyDisk.setText("0");
    this.fRazzberry.setEnabled(false);
    this.fCurrentConfig.setEnabled(false);
    this.autoUpdate.setEnabled(false);
  }
}
