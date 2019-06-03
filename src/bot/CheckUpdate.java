package bot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class CheckUpdate extends JFrame {
  private static final long serialVersionUID = 1L;
  private JPanel container;
  private JLabel textNewVersion;
  private JLabel textCurrent;
  protected JLabel textLatest;
  private JButton download;
  private JButton cancel;
  private JProgressBar progress;
  private int currentRelease = 78;
  private int latestRelease;
  private String currentVersion = "0.7.8";
  private String latestVersion;

  public CheckUpdate() {
    setTitle("jPokeBot Update");
    int width = 350;
    int height = 190;
    setSize(width, height);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);
    setDefaultCloseOperation(3);

    this.container = new JPanel(null);
    add(this.container);
    this.container.setBounds(0, 0, width, height);

    this.textNewVersion = new JLabel("New version detected!");
    this.textLatest = new JLabel("Latest: " + this.latestVersion);
    this.textCurrent = new JLabel("Current: " + this.currentVersion);
    this.download = new JButton("Download");
    this.cancel = new JButton("Cancel");
    this.progress = new JProgressBar();
    this.progress.setStringPainted(true);
    this.progress.setEnabled(false);

    this.container.add(this.textNewVersion);
    this.container.add(this.textLatest);
    this.container.add(this.textCurrent);
    this.container.add(this.progress);
    this.container.add(this.download);
    this.container.add(this.cancel);

    this.textNewVersion.setBounds(10, 10, width - 20, 20);
    this.textLatest.setBounds(10, 40, width - 20, 20);
    this.textCurrent.setBounds(10, 70, width - 20, 20);
    this.progress.setBounds(10, 100, width - 20, 20);
    this.download.setBounds(10, 130, 150, 20);
    this.cancel.setBounds(width - 150 - 10, 130, 150, 20);

    this.download.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CheckUpdate.this.progress.setEnabled(true);
        CheckUpdate.this.download.setEnabled(false);
        CheckUpdate.this.download();
      }
    });

    this.cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FrameLogin fl = new FrameLogin();
        fl.setVisible(true);
        CheckUpdate.this.dispose();
      }
    });
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 0) {
      File toDelete = new File(args[0]);
      toDelete.delete();
    }
    CheckUpdate cu = new CheckUpdate();
    String latest = cu.isUpdated();
    if (latest != null) {
      cu.textLatest.setText("Latest: " + latest);
      cu.setVisible(true);
    } else {
      cu.dispose();
      FrameLogin fl = new FrameLogin();
      fl.setVisible(true);
    }
  }

  private String isUpdated() throws MalformedURLException, IOException, FileNotFoundException {
    URL versionURL = new URL("http://www.jpokebot.altervista.org/latestVersion.html");
    URLConnection yc = versionURL.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
    String remote = in.readLine();
    in.close();
    String[] splitLatest = remote.split("\\|");
    this.latestRelease = Integer.parseInt(splitLatest[1]);
    this.latestVersion = splitLatest[0];
    if (this.currentRelease < this.latestRelease)
      return this.latestVersion;
    return null;
  }

  private void download() {
    Runnable updatethread = new Runnable() {
      public void run() {

        try {
          URL url = new URL("http://www.jpokebot.altervista.org/jpokebot_v" + CheckUpdate.this.latestVersion + ".jar");
          HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
          long completeFileSize = httpConnection.getContentLength();

          BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
          FileOutputStream fos = new FileOutputStream("jpokebot_v" + CheckUpdate.this.latestVersion + ".jar");
          BufferedOutputStream bout = new BufferedOutputStream(fos, '?');
          byte[] data = new byte[1024];
          long downloadedFileSize = 0L;
          int x = 0;
          while ((x = in.read(data, 0, 1024)) >= 0) {
            downloadedFileSize += x;
            final int currentProgress = (int) (downloadedFileSize / completeFileSize * 100.0D);
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                CheckUpdate.this.progress.setValue(currentProgress);
                if (currentProgress == 100) {
                  CheckUpdate.this.downloadCompleted();
                }
              }
            });
            bout.write(data, 0, x);
          }
          bout.close();
          in.close();
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (IOException iOException) {
        }
      }
    };

    (new Thread(updatethread)).start();
  }

  private void downloadCompleted() {
    String toDelete = "";
    try {
      toDelete = CheckUpdate.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    } catch (URISyntaxException e1) {
      e1.printStackTrace();
    }

    try {
      Runtime.getRuntime().exec("java -jar jpokebot_v" + this.latestVersion + ".jar " + toDelete);
    } catch (IOException e) {

      e.printStackTrace();
    }
    dispose();
  }
}
