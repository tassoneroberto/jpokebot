package bot;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class FrameLogin extends JFrame {
  private static final long serialVersionUID = 1L;
  private static HashMap<String, double[]> defaultCoordinates = new HashMap<String, double[]>();
  private JPanel container;
  private JLabel textEmail;
  private JLabel textPassword;
  private JLabel textLatitude;
  private JLabel textLongitude;
  private JLabel loginMessage;
  private JTextField email;
  private JPasswordField password;
  private JTextField latitude;
  private JTextField longitude;
  private JButton login;
  private JButton setCoords;
  private JComboBox<String> defaultCoords;
  private JCheckBox saveData;
  private boolean isAsyncError = true;
  private boolean isWrongInputError = false;

  public FrameLogin() {
    setTitle("jPokeBot");
    int width = 320;
    int height = 270;
    setSize(width, height);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);
    setDefaultCloseOperation(3);

    this.container = new JPanel(null);
    add(this.container);
    this.container.setBounds(0, 0, width, height);

    this.textEmail = new JLabel("Email");
    this.email = new JTextField();
    this.container.add(this.email);
    this.container.add(this.textEmail);
    this.textEmail.setBounds(10, 10, 300, 20);
    this.email.setBounds(10, 30, 300, 20);

    this.textPassword = new JLabel("Password");
    this.password = new JPasswordField();
    this.container.add(this.password);
    this.container.add(this.textPassword);
    this.textPassword.setBounds(10, 50, 300, 20);
    this.password.setBounds(10, 70, 300, 20);

    this.textLatitude = new JLabel("Latitude");
    this.latitude = new JTextField("");
    this.container.add(this.latitude);
    this.container.add(this.textLatitude);
    this.textLatitude.setBounds(10, 90, 150, 20);
    this.latitude.setBounds(10, 110, 150, 20);

    this.textLongitude = new JLabel("Longitude");
    this.longitude = new JTextField("");
    this.container.add(this.longitude);
    this.container.add(this.textLongitude);
    this.textLongitude.setBounds(160, 90, 150, 20);
    this.longitude.setBounds(160, 110, 150, 20);

    this.setCoords = new JButton("Set coordinates");
    this.container.add(this.setCoords);
    this.setCoords.setBounds(10, 140, 150, 20);

    this.defaultCoords = new JComboBox<String>();
    loadDefaultCoordinates();
    this.container.add(this.defaultCoords);
    this.defaultCoords.setBounds(160, 135, 150, 30);

    this.login = new JButton("Login");
    this.container.add(this.login);
    this.login.setBounds(10, 180, 200, 20);

    this.saveData = new JCheckBox("Save data");
    this.container.add(this.saveData);
    this.saveData.setSelected(true);
    this.saveData.setBounds(220, 177, 100, 25);

    this.loginMessage = new JLabel("Enter your login data to connect.");
    this.container.add(this.loginMessage);
    this.loginMessage.setBounds(10, 210, 300, 20);

    this.setCoords.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double[] coords = (double[]) defaultCoordinates.get(FrameLogin.this.defaultCoords.getSelectedItem());
        FrameLogin.this.latitude.setText(coords[0] + "");
        FrameLogin.this.longitude.setText(coords[1] + "");
      }
    });

    this.login.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FrameLogin.this.login();
      }
    });

    this.email.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == 10) {
          FrameLogin.this.login();
        }
      }
    });

    this.password.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == 10) {
          FrameLogin.this.login();
        }
      }
    });

    this.latitude.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == 10) {
          FrameLogin.this.login();
        }
      }
    });

    this.longitude.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == 10) {
          FrameLogin.this.login();
        }
      }
    });

    File dir = new File("jpokebot/");
    if (!dir.exists()) {
      dir.mkdirs();
    }

    File user = new File("jpokebot/user");
    if (!user.exists()) {
      user.mkdirs();
    }

    File changelogFile = new File("jpokebot/changelog.txt");
    if (!changelogFile.exists()) {
      try {
        changelogFile.createNewFile();
        FileWriter fw = new FileWriter(changelogFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getChangelog());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    File loginDataFile = new File("jpokebot/user/loginData.txt");
    if (!loginDataFile.exists()) {
      try {
        loginDataFile.createNewFile();
        FileWriter fw = new FileWriter(loginDataFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDefaultLogin());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } else {
      loadLoginData();
    }

    File defaultRecycleFile = new File("jpokebot/user/settingsItemRecycle.txt");
    if (!defaultRecycleFile.exists()) {
      try {
        defaultRecycleFile.createNewFile();
        FileWriter fw = new FileWriter(defaultRecycleFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDefaultSettingsRecycle());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    File defaultEggHatchFile = new File("jpokebot/user/settingsEggHatch.txt");
    if (!defaultEggHatchFile.exists()) {
      try {
        defaultEggHatchFile.createNewFile();
        FileWriter fw = new FileWriter(defaultEggHatchFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDefaultEggHatch());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    File defaultGeneralFile = new File("jpokebot/user/settingsGeneral.txt");
    if (!defaultGeneralFile.exists()) {
      try {
        defaultGeneralFile.createNewFile();
        FileWriter fw = new FileWriter(defaultGeneralFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDefaultGeneralSettings());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    File defaultPokemonCatchFile = new File("jpokebot/user/settingsPokemonCatch.txt");
    if (!defaultPokemonCatchFile.exists()) {
      try {
        defaultPokemonCatchFile.createNewFile();
        FileWriter fw = new FileWriter(defaultPokemonCatchFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDefaultSettingsPokemonCatch());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    File defaultTransferFile = new File("jpokebot/user/settingsTransfer.txt");
    if (!defaultTransferFile.exists()) {
      try {
        defaultTransferFile.createNewFile();
        FileWriter fw = new FileWriter(defaultTransferFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDefaultSettingsTransfer());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    File defaultRenameFile = new File("jpokebot/user/settingsRename.txt");
    if (!defaultRenameFile.exists()) {
      try {
        defaultRenameFile.createNewFile();
        FileWriter fw = new FileWriter(defaultRenameFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDefaultSettingsRename());
        bw.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
  }

  private void loadDefaultCoordinates() {
    double[] sidneyCoords = { -33.86302591709663D, 151.2098517268896D };
    defaultCoordinates.put("[AU] Sidney", sidneyCoords);

    double[] hongKongCoords = { 22.285661566028704D, 114.15020227432251D };
    defaultCoordinates.put("[CN] Hong Kong", hongKongCoords);

    double[] tokyoCoords = { 35.684895044528794D, 139.70996975898743D };
    defaultCoordinates.put("[JP] Tokyo", tokyoCoords);

    double[] newYorkCoords = { 40.7711329D, -73.9741874D };
    defaultCoordinates.put("[US] New York", newYorkCoords);

    double[] santaMonicaCoords = { 34.01478835307631D, -118.4959434915362D };
    defaultCoordinates.put("[US] Santa Monica", santaMonicaCoords);

    double[] californiaCoords = { 34.0092419D, -118.49760370000001D };
    defaultCoordinates.put("[US] California", californiaCoords);

    double[] berlinCoords = { 52.537023D, 13.203011D };
    defaultCoordinates.put("[EU] Berlin", berlinCoords);

    double[] milanCoords = { 45.4721D, 9.17722D };
    defaultCoordinates.put("[EU] Milan", milanCoords);

    for (String location : defaultCoordinates.keySet()) {
      this.defaultCoords.addItem(location);
    }
  }

  private String getDefaultLogin() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/res/config/defaultLoginData.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private String getDefaultSettingsRecycle() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/res/config/defaultSettingsItemRecycle.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private String getDefaultGeneralSettings() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/res/config/defaultSettingsGeneral.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private String getDefaultSettingsPokemonCatch() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/res/config/defaultSettingsPokemonCatch.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private String getDefaultSettingsTransfer() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/res/config/defaultSettingsTransfer.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private String getDefaultSettingsRename() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/res/config/defaultSettingsRename.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private String getDefaultEggHatch() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/res/config/defaultSettingsEggHatch.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  private String getChangelog() {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("/changelog.txt")));
    StringBuilder sb = new StringBuilder();
    try {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(String.valueOf(line) + System.lineSeparator());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  protected String checkLoginData() {
    if (this.email.getText().equals("") || this.password.getPassword().toString().equals("")
        || this.latitude.getText().equals("") || this.longitude.getText().equals("")) {
      return "Error: all fields are required.";
    }
    boolean coordinatesOK = true;
    try {
      Double.parseDouble(this.latitude.getText());
      Double.parseDouble(this.longitude.getText());
    } catch (NumberFormatException e) {
      coordinatesOK = false;
    }
    if (!coordinatesOK) {
      return "Error: check your coordinates.";
    }
    return "";
  }

  private void loadLoginData() {
    try {
      BufferedReader br = new BufferedReader(
        new InputStreamReader(getClass().getResourceAsStream("jpokebotuser/loginData.txt")));
      String line;
      while ((line = br.readLine()) != null) {
        String[] info = line.split("=");
        if (info.length == 2 && !info[1].equals("")) {
          if (info[0].equals("email")) {
            this.email.setText(info[1]);
            continue;
          }
          if (info[0].equals("password")) {
            this.password.setText(info[1]);
            continue;
          }
          if (info[0].equals("latitude")) {
            this.latitude.setText(info[1]);
            continue;
          }
          if (info[0].equals("longitude")) {
            this.longitude.setText(info[1]);
            continue;
          }
          if (info[0].equals("saveLoginData") && info[1].equals("true")) {
            this.saveData.setSelected(true);
          }
        }
      }
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }

  protected void login() {
    this.isAsyncError = true;
    this.isWrongInputError = false;
    LoginLoader loader = new LoginLoader();
    disableButtons();
    this.loginMessage.setText("Connecting...");
    loader.start();
  }

  private void disableButtons() {
    this.login.setEnabled(false);
    this.email.setEnabled(false);
    this.password.setEnabled(false);
    this.latitude.setEnabled(false);
    this.longitude.setEnabled(false);
    this.setCoords.setEnabled(false);
    this.defaultCoords.setEnabled(false);
    this.saveData.setEnabled(false);
  }

  private void enableButtons() {
    this.login.setEnabled(true);
    this.email.setEnabled(true);
    this.password.setEnabled(true);
    this.latitude.setEnabled(true);
    this.longitude.setEnabled(true);
    this.setCoords.setEnabled(true);
    this.defaultCoords.setEnabled(true);
    this.saveData.setEnabled(true);
  }

  public class LoginLoader extends Thread {
    public void run() {
      while (FrameLogin.this.isAsyncError && !FrameLogin.this.isWrongInputError) {
        String loginDataError = FrameLogin.this.checkLoginData();
        if (!loginDataError.equals("")) {
          FrameLogin.this.loginMessage.setText(loginDataError);
          FrameLogin.this.isWrongInputError = true;
          continue;
        }
        FrameLogin.this.isWrongInputError = false;
        FrameLogin.this.isAsyncError = false;
        PokemonGo go = null;
        try {
          go = Bot.connect(FrameLogin.this.email.getText(), new String(FrameLogin.this.password.getPassword()));
        } catch (LoginFailedException e1) {
          FrameLogin.this.loginMessage.setText("Wrong input data!");
          FrameLogin.this.enableButtons();
          FrameLogin.this.isWrongInputError = true;
        } catch (RemoteServerException e1) {
          FrameLogin.this.loginMessage.setText("Server unreachable! Retrying...");
          FrameLogin.this.isAsyncError = true;
        } catch (AsyncPokemonGoException e1) {
          FrameLogin.this.loginMessage.setText("Async exception! Retrying...");
          FrameLogin.this.isAsyncError = true;
        }
        if (!FrameLogin.this.isAsyncError && !FrameLogin.this.isWrongInputError) {
          if (FrameLogin.this.saveData.isSelected())
            FrameLogin.this.saveLoginData();
          FrameLogin.this.inzializeBot(go, Double.parseDouble(FrameLogin.this.latitude.getText()),
              Double.parseDouble(FrameLogin.this.longitude.getText()));
        }
      }
    }
  }

  private void saveLoginData() {
    StringBuilder sb = new StringBuilder();
    sb.append("jPokeBot - LOGIN DATA" + System.lineSeparator() + System.lineSeparator());
    sb.append("email=" + this.email.getText() + System.lineSeparator());
    sb.append("password=" + new String(this.password.getPassword()) + System.lineSeparator());
    sb.append("latitude=" + this.latitude.getText() + System.lineSeparator());
    sb.append("longitude=" + this.longitude.getText() + System.lineSeparator());
    sb.append("saveLoginData=true");

    File loginDataFile = new File("jpokebotuser/loginData.txt");

    try {
      FileWriter fw = new FileWriter(loginDataFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void inzializeBot(PokemonGo go, double latitude, double longitude) {
    setVisible(false);
    FrameBot fb = new FrameBot(go, latitude, longitude);
    fb.setVisible(true);
  }

  public static void main(String[] args) {
    JFrame f = new FrameLogin();
    f.setVisible(true);
  }
}
