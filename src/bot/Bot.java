package bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

import javax.swing.JTextArea;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.EggIncubator;
import com.pokegoapi.api.inventory.Item;
import com.pokegoapi.api.inventory.Pokeball;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.api.pokemon.EggPokemon;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.api.settings.CatchOptions;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.exceptions.AsyncPokemonGoException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.PokeDictionary;

import POGOProtos.Inventory.Item.ItemAwardOuterClass;
import POGOProtos.Inventory.Item.ItemIdOuterClass;
import POGOProtos.Inventory.Item.ItemIdOuterClass.ItemId;
import okhttp3.OkHttpClient;

public class Bot {
	public static final String VERSION = "0.7.8";
	public static final int NUMBER_RELEASE = 78;
	private PokemonGo go;
	private Thread farmer;
	private JTextArea display;
	private boolean isActiveRecycle;
	private boolean isRecycleWhenFull;
	private boolean isKeepIncense;
	private boolean isKeepMasterball;
	private boolean isKeepTroyDisk;
	private boolean isKeepLuckyEgg;
	private boolean isAutoUpdate;
	private int kPokeball;
	private int kMegaball;
	private int kUltraball;
	private int kMasterball;
	private int kLuckyEgg;
	private int kIncense;
	private int kTroyDisk;
	private int kRazzBerry;
	private int kPotion;
	private int kSuperPotion;
	private int kHyperPotion;
	private int kMaxPotion;
	private int kRevive;
	private int kMaxRevive;
	private boolean isGetLevelReward = true;
	private int scanWidth;
	private int timeToCollect;
	private int rescanTimeOut;
	private int speed;
	private HashSet<Integer> levelChangeCollect;
	private boolean isActiveCatch;
	private boolean isUseMasterballOnLegendary;
	private Locale locale = new Locale("en");
	private boolean isLimitPokeball;
	private boolean isUseBerry;
	private boolean isLimitBerry;
	private String pokeballSelection;
	private Pokeball defaultPokeball;
	private int maxPokeball;
	private int maxBerry;
	private int timeOutCatch;
	private HashSet<Integer> legendary;
	private boolean isActiveTransfer;
	private boolean isMinCP;
	private boolean isMinIV;
	private boolean isActiveSmartTransfer;
	private boolean isNeverTransferLegendary;
	private boolean isNeverTransferFavorite;
	private boolean isNeverTransferPerfectMoves;
	private int minCP;
	private int minIV;
	private int minAverage;
	private int maxDuplicate;
	private int priority;
	private HashMap<Integer, List<Pokemon>> idPokemonLists;
	private Collection<Pokemon> pokebank;
	private boolean isActiveRename;
	private boolean isRenameIV;
	private boolean isRenamePerfectMoves;
	private boolean isActiveEggHatch;
	private boolean isHatchAll;
	private boolean isDescendant;
	private int selectedEggKm;
	private int level;
	private HashMap<ItemId, Integer> items;
	private FrameBot frameBot;
	private CatchOptions catchOptions;
	private String separator = "-------------------------------------------------------\n";
	private HashSet<Integer> fullyEvolved;
	boolean availableIncubator = false;
	boolean availableEgg = false;
	int softbanWaitingTime = 5;
	long initialExp;

	public Bot(PokemonGo go, JTextArea display, FrameBot frameBot) {
		this.frameBot = frameBot;
		this.display = display;
		this.go = go;
	}

	public Bot(PokemonGo go, JTextArea display) {
		this.display = display;
		this.go = go;
	}

	public Bot(PokemonGo go) {
		this.go = go;
	}

	public static void main(String[] args) throws LoginFailedException, RemoteServerException, InterruptedException {
		FrameLogin fl = new FrameLogin();
		fl.setVisible(true);
	}

	public void startBot() {
		this.farmer = new PokeBot();
		this.farmer.start();
	}

	double time = 1.0D;
	Collection<Pokestop> defaultPokestop = null;

	public void runBot() {
		this.initialExp = this.go.getPlayerProfile().getStats().getExperience();
		this.display.append("Starting jPokeBot...\n");
		loadSettingsGeneral();
		this.display.append("General settings loaded!\n");
		this.level = this.go.getPlayerProfile().getStats().getLevel();
		loadSettingsRecycle();
		this.levelChangeCollect = new HashSet<Integer>();
		this.levelChangeCollect.add(Integer.valueOf(5));
		this.levelChangeCollect.add(Integer.valueOf(8));
		this.levelChangeCollect.add(Integer.valueOf(10));
		this.levelChangeCollect.add(Integer.valueOf(12));
		this.levelChangeCollect.add(Integer.valueOf(15));
		this.levelChangeCollect.add(Integer.valueOf(20));
		this.levelChangeCollect.add(Integer.valueOf(25));
		this.levelChangeCollect.add(Integer.valueOf(30));
		this.display.append("Item recycling settings loaded!\n");
		loadLegendaries();
		loadFullyEvolved();
		loadSettingsPokemonCatch();
		this.display.append("Pokemon catching settings loaded!\n");
		loadSettingsTransfer();
		this.display.append("Pokemon transfer settings loaded!\n");
		if (this.isActiveTransfer) {
			loadPokebank();
			transferPokemon();
			try {
				this.go.getInventories().updateInventories(true);
			} catch (LoginFailedException e) {
				e.printStackTrace();
			} catch (RemoteServerException e) {
				e.printStackTrace();
			}
			this.display.append("Pokebank loaded!\n");
		}
		loadSettingsPokemonRename();
		this.display.append("Pokemon rename settings loaded!\n");
		if (this.isActiveRename)
			renameAllPokemon();
		loadSettingsEggHatch();
		this.display.append("Egg hatch settings loaded!\n");
		if (this.isActiveEggHatch)
			incubateEggs();
		this.display.append("Location set to " + this.go.getLatitude() + " | " + this.go.getLongitude() + "\n");
		this.items = new HashMap<ItemId, Integer>();

		updateInventory();
		this.display.append("Getting all near Pokestop...\n");

		try {
			this.defaultPokestop = this.go.getMap().getMapObjects().getPokestops();
		} catch (LoginFailedException e1) {
			e1.printStackTrace();
		} catch (RemoteServerException e1) {
			e1.printStackTrace();
		}
		this.display.append("Total Pokestop found: " + this.defaultPokestop.size() + "\n");

		List<CatchablePokemon> catchablePokemon = null;
		if (this.isActiveCatch) {
			try {
				catchablePokemon = this.go.getMap().getCatchablePokemon();
				if (catchablePokemon.size() > 0) {
					catchPokemons(catchablePokemon);
				}
			} catch (LoginFailedException e) {
				e.printStackTrace();
			} catch (RemoteServerException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		Collection<Pokestop> lootablePokestop = getLootablePokestop(this.go, this.defaultPokestop);
		if (lootablePokestop.size() > 0) {
			for (Pokestop ps : lootablePokestop) {
				if (ps.canLoot()) {
					try {
						lootPokestop(this.go, ps);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		while (true) {
			incubateEggs();
			lootablePokestop = getLootablePokestop(this.go, this.defaultPokestop);
			HashMap<Pokestop, Double> distancePokestop = new HashMap<Pokestop, Double>();
			for (Pokestop p : this.defaultPokestop) {
				distancePokestop.put(p, Double.valueOf(p.getDistance()));
			}
			while (lootablePokestop.size() != 0) {
				try {
					this.go.getInventories().updateInventories(true);
				} catch (LoginFailedException e1) {
					e1.printStackTrace();
				} catch (RemoteServerException e1) {
					e1.printStackTrace();
				}
				this.frameBot.updateUserInfo();
				Pokestop nearestPokestop = null;
				double minDistance = Double.MAX_VALUE;
				for (Pokestop p : lootablePokestop) {
					if (this.defaultPokestop.contains(p)
							&& ((Double) distancePokestop.get(p)).doubleValue() < minDistance) {
						nearestPokestop = p;
						minDistance = ((Double) distancePokestop.get(p)).doubleValue();
					}
				}

				if (nearestPokestop == null) {
					this.display.append("No more lootable Pokestop available. Rescanning in " + this.rescanTimeOut
							+ " seconds...\n");
					try {
						Thread.sleep((this.rescanTimeOut * 1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.time += this.rescanTimeOut;
					continue;
				}
				this.display.append(this.separator);
				try {
					this.display.append("Pokestop: " + nearestPokestop.getDetails().getName() + "\n");
				} catch (LoginFailedException e1) {
					e1.printStackTrace();
				} catch (RemoteServerException e1) {
					e1.printStackTrace();
				}
				double toLat = nearestPokestop.getLatitude();
				double toLon = nearestPokestop.getLongitude();
				double distance = nearestPokestop.getDistance() / 1000.0D;
				double timeToArrive = distance / this.speed * 3600.0D;
				int approxTimeToArrive = (int) timeToArrive;
				int approxDistance = (int) (distance * 1000.0D);
				this.display
						.append("Moving... " + approxTimeToArrive + " seconds to arrive. [" + approxDistance + "m]\n");
				try {
					Thread.sleep((long) (timeToArrive * 1000.0D));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				this.time += timeToArrive;
				this.go.setLatitude(toLat);
				this.go.setLongitude(toLon);

				if (this.isActiveCatch) {
					try {
						try {
							catchablePokemon = this.go.getMap().getCatchablePokemon();
						} catch (LoginFailedException e) {
							e.printStackTrace();
						} catch (RemoteServerException e) {
							e.printStackTrace();
						}
					} catch (AsyncPokemonGoException e) {
						e.printStackTrace();
					}
					try {
						catchPokemons(catchablePokemon);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				this.display.append("Looting... ");
				try {
					lootPokestop(this.go, nearestPokestop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				lootablePokestop = getLootablePokestop(this.go, this.defaultPokestop);
				for (Pokestop p : this.defaultPokestop) {
					distancePokestop.put(p, Double.valueOf(p.getDistance()));
				}
			}

			this.display.append(
					"No more lootable Pokestop available. Rescanning in " + this.rescanTimeOut + " seconds...\n");
			try {
				Thread.sleep((this.rescanTimeOut * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.time += this.rescanTimeOut;
		}
	}

	private void incubateEggs() {
		try {
			this.go.getInventories().updateInventories(true);
		} catch (LoginFailedException e) {
			e.printStackTrace();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		}
		List<EggIncubator> freeIncubators = new LinkedList<EggIncubator>();
		for (EggIncubator e : this.go.getInventories().getIncubators()) {
			try {
				if (!e.isInUse())
					freeIncubators.add(e);
			} catch (LoginFailedException | RemoteServerException e1) {
				e1.printStackTrace();
			}
		}
		if (freeIncubators.size() != 0) {
			this.availableIncubator = true;
			HashMap<Integer, LinkedList<EggPokemon>> freeEgg = new HashMap<Integer, LinkedList<EggPokemon>>();
			for (EggPokemon egg : this.go.getInventories().getHatchery().getEggs()) {
				if (!egg.isIncubate()) {
					LinkedList<EggPokemon> temp;
					if (!freeEgg.containsKey(Integer.valueOf((int) egg.getEggKmWalkedTarget()))) {
						temp = new LinkedList<EggPokemon>();
						temp.add(egg);
					} else {
						temp = (LinkedList) freeEgg.get(Integer.valueOf((int) egg.getEggKmWalkedTarget()));
						temp.add(egg);
					}
					freeEgg.put(Integer.valueOf((int) egg.getEggKmWalkedTarget()), temp);
				}
			}
			if (freeEgg.size() != 0) {
				this.availableEgg = true;
				while (this.availableIncubator && this.availableEgg) {
					EggPokemon eggToIncubate = null;
					EggIncubator incubatorToUse = null;
					if (!this.isHatchAll) {
						if (freeEgg.containsKey(Integer.valueOf(this.selectedEggKm))) {
							eggToIncubate = (EggPokemon) ((LinkedList) freeEgg.get(Integer.valueOf(this.selectedEggKm)))
									.removeFirst();
							if (((LinkedList) freeEgg.get(Integer.valueOf(this.selectedEggKm))).size() == 0)
								freeEgg.remove(Integer.valueOf(this.selectedEggKm));
							incubatorToUse = (EggIncubator) freeIncubators.remove(0);
							if (freeIncubators.size() == 0)
								this.availableIncubator = false;
							incubateEgg(eggToIncubate, incubatorToUse);
							continue;
						}
						this.availableEgg = false;
						continue;
					}
					if (this.isDescendant) {
						int maxKmEgg = 0;
						for (Integer i : freeEgg.keySet()) {
							if (i.intValue() > maxKmEgg)
								maxKmEgg = i.intValue();
						}
						eggToIncubate = (EggPokemon) ((LinkedList) freeEgg.get(Integer.valueOf(maxKmEgg)))
								.removeFirst();
						if (((LinkedList) freeEgg.get(Integer.valueOf(maxKmEgg))).size() == 0)
							freeEgg.remove(Integer.valueOf(maxKmEgg));
					} else {
						int minKmEgg = 0;
						for (Integer i : freeEgg.keySet()) {
							if (i.intValue() < minKmEgg)
								minKmEgg = i.intValue();
						}
						eggToIncubate = (EggPokemon) ((LinkedList) freeEgg.get(Integer.valueOf(minKmEgg)))
								.removeFirst();
						if (((LinkedList) freeEgg.get(Integer.valueOf(minKmEgg))).size() == 0)
							freeEgg.remove(Integer.valueOf(minKmEgg));
					}
					incubatorToUse = (EggIncubator) freeIncubators.remove(0);
					incubateEgg(eggToIncubate, incubatorToUse);
				}
			}
		}
	}

	private void incubateEgg(EggPokemon eggToIncubate, EggIncubator incubatorToUse) {
		try {
			eggToIncubate.incubate(incubatorToUse);
		} catch (LoginFailedException e) {
			e.printStackTrace();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		}
		this.display.append("Incubated a " + eggToIncubate.getEggKmWalkedTarget() + " egg.\n");

		try {
			this.go.getInventories().updateInventories(true);
		} catch (LoginFailedException e) {
			e.printStackTrace();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		}

		for (EggPokemon egg : this.go.getInventories().getHatchery().getEggs()) {
			if (!egg.isIncubate())
				this.availableEgg = true;
		}
		for (EggIncubator e : this.go.getInventories().getIncubators()) {
			try {
				if (!e.isInUse())
					this.availableIncubator = true;
			} catch (LoginFailedException | RemoteServerException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void loadLegendaries() {
		this.legendary = new HashSet<Integer>();
		this.legendary.add(Integer.valueOf(132));
		this.legendary.add(Integer.valueOf(144));
		this.legendary.add(Integer.valueOf(145));
		this.legendary.add(Integer.valueOf(146));
		this.legendary.add(Integer.valueOf(150));
		this.legendary.add(Integer.valueOf(151));
	}

	private void loadFullyEvolved() {
		this.fullyEvolved = new HashSet<Integer>();
		this.fullyEvolved.add(Integer.valueOf(3));
		this.fullyEvolved.add(Integer.valueOf(6));
		this.fullyEvolved.add(Integer.valueOf(9));
		this.fullyEvolved.add(Integer.valueOf(12));
		this.fullyEvolved.add(Integer.valueOf(15));
		this.fullyEvolved.add(Integer.valueOf(18));
		this.fullyEvolved.add(Integer.valueOf(20));
		this.fullyEvolved.add(Integer.valueOf(22));
		this.fullyEvolved.add(Integer.valueOf(24));
		this.fullyEvolved.add(Integer.valueOf(26));
		this.fullyEvolved.add(Integer.valueOf(28));
		this.fullyEvolved.add(Integer.valueOf(31));
		this.fullyEvolved.add(Integer.valueOf(34));
		this.fullyEvolved.add(Integer.valueOf(36));
		this.fullyEvolved.add(Integer.valueOf(38));
		this.fullyEvolved.add(Integer.valueOf(40));
		this.fullyEvolved.add(Integer.valueOf(45));
		this.fullyEvolved.add(Integer.valueOf(47));
		this.fullyEvolved.add(Integer.valueOf(49));
		this.fullyEvolved.add(Integer.valueOf(51));
		this.fullyEvolved.add(Integer.valueOf(53));
		this.fullyEvolved.add(Integer.valueOf(55));
		this.fullyEvolved.add(Integer.valueOf(57));
		this.fullyEvolved.add(Integer.valueOf(59));
		this.fullyEvolved.add(Integer.valueOf(62));
		this.fullyEvolved.add(Integer.valueOf(65));
		this.fullyEvolved.add(Integer.valueOf(68));
		this.fullyEvolved.add(Integer.valueOf(71));
		this.fullyEvolved.add(Integer.valueOf(73));
		this.fullyEvolved.add(Integer.valueOf(76));
		this.fullyEvolved.add(Integer.valueOf(78));
		this.fullyEvolved.add(Integer.valueOf(80));
		this.fullyEvolved.add(Integer.valueOf(83));
		this.fullyEvolved.add(Integer.valueOf(85));
		this.fullyEvolved.add(Integer.valueOf(87));
		this.fullyEvolved.add(Integer.valueOf(89));
		this.fullyEvolved.add(Integer.valueOf(91));
		this.fullyEvolved.add(Integer.valueOf(94));
		this.fullyEvolved.add(Integer.valueOf(97));
		this.fullyEvolved.add(Integer.valueOf(99));
		this.fullyEvolved.add(Integer.valueOf(101));
		this.fullyEvolved.add(Integer.valueOf(103));
		this.fullyEvolved.add(Integer.valueOf(105));
		this.fullyEvolved.add(Integer.valueOf(106));
		this.fullyEvolved.add(Integer.valueOf(107));
		this.fullyEvolved.add(Integer.valueOf(110));
		this.fullyEvolved.add(Integer.valueOf(115));
		this.fullyEvolved.add(Integer.valueOf(119));
		this.fullyEvolved.add(Integer.valueOf(121));
		this.fullyEvolved.add(Integer.valueOf(122));
		this.fullyEvolved.add(Integer.valueOf(124));
		this.fullyEvolved.add(Integer.valueOf(127));
		this.fullyEvolved.add(Integer.valueOf(128));
		this.fullyEvolved.add(Integer.valueOf(130));
		this.fullyEvolved.add(Integer.valueOf(131));
		this.fullyEvolved.add(Integer.valueOf(132));
		this.fullyEvolved.add(Integer.valueOf(134));
		this.fullyEvolved.add(Integer.valueOf(135));
		this.fullyEvolved.add(Integer.valueOf(136));
		this.fullyEvolved.add(Integer.valueOf(139));
		this.fullyEvolved.add(Integer.valueOf(141));
		this.fullyEvolved.add(Integer.valueOf(142));
		this.fullyEvolved.add(Integer.valueOf(143));
		this.fullyEvolved.add(Integer.valueOf(144));
		this.fullyEvolved.add(Integer.valueOf(145));
		this.fullyEvolved.add(Integer.valueOf(146));
		this.fullyEvolved.add(Integer.valueOf(149));
		this.fullyEvolved.add(Integer.valueOf(150));
		this.fullyEvolved.add(Integer.valueOf(151));
	}

	public void stopBot() {
		this.farmer.interrupt();
	}

	public class PokeBot extends Thread {
		public void run() {
			Bot.this.runBot();
		}
	}

	public static PokemonGo connect(String email, String password) throws LoginFailedException, RemoteServerException {
		OkHttpClient httpClient = new OkHttpClient();
		GoogleAutoCredentialProvider provider = new GoogleAutoCredentialProvider(httpClient, email, password);
		PokemonGo go = new PokemonGo(httpClient);
		go.login(provider);
		return go;
	}

	private void loadSettingsRecycle() {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsItemRecycle.txt")));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				String[] info = line.split("=");
				if (info.length == 2 && !info[1].equals("")) {
					if (info[0].equals("active")) {
						if (info[1].equals("true")) {
							this.isActiveRecycle = true;
							continue;
						}
						this.isActiveRecycle = false;
						continue;
					}
					if (info[0].equals("autoUpdate")) {
						if (info[1].equals("true")) {
							this.isAutoUpdate = true;
							continue;
						}
						this.isAutoUpdate = false;
						continue;
					}
					if (info[0].equals("recycleWhenFull")) {
						if (info[1].equals("true")) {
							this.isRecycleWhenFull = true;
							continue;
						}
						this.isRecycleWhenFull = false;
						continue;
					}
					if (info[0].equals("keepMasterball")) {
						if (info[1].equals("true")) {
							this.isKeepMasterball = true;
							continue;
						}
						this.isKeepMasterball = false;
						continue;
					}
					if (info[0].equals("keepIncense")) {
						if (info[1].equals("true")) {
							this.isKeepIncense = true;
							continue;
						}
						this.isKeepIncense = false;
						continue;
					}
					if (info[0].equals("keepTroyDisk")) {
						if (info[1].equals("true")) {
							this.isKeepTroyDisk = true;
							continue;
						}
						this.isKeepTroyDisk = false;
						continue;
					}
					if (info[0].equals("keepLuckyEgg")) {
						if (info[1].equals("true")) {
							this.isKeepLuckyEgg = true;
							continue;
						}
						this.isKeepLuckyEgg = false;
						continue;
					}
					if (info[0].equals("pokeball")) {
						this.kPokeball = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("megaball")) {
						this.kMegaball = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("ultraball")) {
						this.kUltraball = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("masterball")) {
						if (info[1].equals("infinity")) {
							this.kMasterball = Integer.MAX_VALUE;
							continue;
						}
						this.kMasterball = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("potion")) {
						this.kPotion = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("superpotion")) {
						this.kSuperPotion = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("hyperpotion")) {
						this.kHyperPotion = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("maxpotion")) {
						this.kMaxPotion = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("revive")) {
						this.kRevive = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("maxrevive")) {
						this.kMaxRevive = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("razzberry")) {
						this.kRazzBerry = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("incense")) {
						if (info[1].equals("infinity")) {
							this.kIncense = Integer.MAX_VALUE;
							continue;
						}
						this.kIncense = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("troydisk")) {
						if (info[1].equals("infinity")) {
							this.kTroyDisk = Integer.MAX_VALUE;
							continue;
						}
						this.kTroyDisk = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("luckyegg")) {
						if (info[1].equals("infinity")) {
							this.kLuckyEgg = Integer.MAX_VALUE;
							continue;
						}
						this.kLuckyEgg = Integer.parseInt(info[1]);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadSettingsGeneral() {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsGeneral.txt")));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				String[] info = line.split("=");
				if (info.length == 2 && !info[1].equals("")) {
					if (info[0].equals("width")) {
						this.scanWidth = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("timeToCollect")) {
						this.timeToCollect = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("rescanTimeOut")) {
						this.rescanTimeOut = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("speed")) {
						this.speed = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("getLevelReward")) {
						if (info[1].equals("true")) {
							this.isGetLevelReward = true;
							continue;
						}
						if (info[1].equals("false"))
							this.isGetLevelReward = false;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.go.getMap().setDefaultWidth(this.scanWidth);
	}

	private void loadSettingsTransfer() {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsTransfer.txt")));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				String[] info = line.split("=");
				if (info.length == 2 && !info[1].equals("")) {
					if (info[0].equals("active")) {
						if (info[1].equals("true")) {
							this.isActiveTransfer = true;
							continue;
						}
						this.isActiveTransfer = false;
						continue;
					}
					if (info[0].equals("minCP")) {
						this.minCP = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("minIV")) {
						this.minIV = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("keepMinCP")) {
						if (info[1].equals("true")) {
							this.isMinCP = true;
							continue;
						}
						this.isMinCP = false;
						continue;
					}
					if (info[0].equals("keepMinIV")) {
						if (info[1].equals("true")) {
							this.isMinIV = true;
							continue;
						}
						this.isMinIV = false;
						continue;
					}
					if (info[0].equals("keepLegendaries")) {
						if (info[1].equals("true")) {
							this.isNeverTransferLegendary = true;
							continue;
						}
						this.isNeverTransferLegendary = false;
						continue;
					}
					if (info[0].equals("keepFavorites")) {
						if (info[1].equals("true")) {
							this.isNeverTransferFavorite = true;
							continue;
						}
						this.isNeverTransferFavorite = false;
						continue;
					}
					if (info[0].equals("keepPerfectMoves")) {
						if (info[1].equals("true")) {
							this.isNeverTransferPerfectMoves = true;
							continue;
						}
						this.isNeverTransferPerfectMoves = false;
						continue;
					}
					if (info[0].equals("activeSmartTransfer")) {
						if (info[1].equals("true")) {
							this.isActiveSmartTransfer = true;
							continue;
						}
						this.isActiveSmartTransfer = false;
						continue;
					}
					if (info[0].equals("priority")) {
						this.priority = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("maxDuplicate")) {
						this.maxDuplicate = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("minAverage"))
						this.minAverage = Integer.parseInt(info[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadSettingsPokemonCatch() {
		this.catchOptions = new CatchOptions(this.go);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsPokemonCatch.txt")));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				String[] info = line.split("=");
				if (info.length == 2 && !info[1].equals("")) {
					if (info[0].equals("active")) {
						if (info[1].equals("true")) {
							this.isActiveCatch = true;
							continue;
						}
						this.isActiveCatch = false;
						continue;
					}
					if (info[0].equals("timeOutCatch")) {
						this.timeOutCatch = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("useBerry")) {
						if (info[1].equals("true")) {
							this.isUseBerry = true;
							continue;
						}
						this.isUseBerry = false;
						continue;
					}
					if (info[0].equals("useMasterballOnLegendary")) {
						if (info[1].equals("true")) {
							this.isUseMasterballOnLegendary = true;
							continue;
						}
						this.isUseMasterballOnLegendary = false;
						continue;
					}
					if (info[0].equals("pokeballToUse")) {
						this.pokeballSelection = info[1];
						continue;
					}
					if (info[0].equals("defaultPokeball")) {
						if (info[1].equals("Pokeball")) {
							this.defaultPokeball = Pokeball.POKEBALL;
							continue;
						}
						if (info[1].equals("Megaball")) {
							this.defaultPokeball = Pokeball.GREATBALL;
							continue;
						}
						if (info[1].equals("Ultraball")) {
							this.defaultPokeball = Pokeball.ULTRABALL;
							continue;
						}
						if (info[1].equals("Masterball"))
							this.defaultPokeball = Pokeball.MASTERBALL;
						continue;
					}
					if (info[0].equals("limitBerryUsed")) {
						if (info[1].equals("true")) {
							this.isLimitBerry = true;
							continue;
						}
						this.isLimitBerry = false;
						continue;
					}
					if (info[0].equals("limitPokeballUsed")) {
						if (info[1].equals("true")) {
							this.isLimitPokeball = true;
							continue;
						}
						this.isLimitPokeball = false;
						continue;
					}
					if (info[0].equals("maxBerryUsed")) {
						this.maxBerry = Integer.parseInt(info[1]);
						continue;
					}
					if (info[0].equals("maxPokeballUsed"))
						this.maxPokeball = Integer.parseInt(info[1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.catchOptions.useRazzberries(this.isUseBerry);
		if (this.isLimitBerry)
			this.catchOptions.maxRazzberries(this.maxBerry);
		if (this.isLimitPokeball)
			this.catchOptions.maxPokeballs(this.maxPokeball);
		if (this.pokeballSelection.equals("smart")) {
			this.catchOptions.useSmartSelect(true);
		} else if (this.pokeballSelection.equals("best")) {
			this.catchOptions.useBestBall(true);
		} else if (this.pokeballSelection.equals("custom")) {
			this.catchOptions.usePokeball(this.defaultPokeball);
		}
	}

	private void loadSettingsPokemonRename() {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsRename.txt")));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				String[] info = line.split("=");
				if (info.length == 2 && !info[1].equals("")) {
					if (info[0].equals("active"))
						if (info[1].equals("true")) {
							this.isActiveRename = true;
						} else {
							this.isActiveRename = false;
						}
					if (info[0].equals("renameIV"))
						if (info[1].equals("true")) {
							this.isRenameIV = true;
						} else {
							this.isRenameIV = false;
						}
					if (info[0].equals("renamePerfectMoves")) {
						if (info[1].equals("true")) {
							this.isRenamePerfectMoves = true;
							continue;
						}
						this.isRenamePerfectMoves = false;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadSettingsEggHatch() {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/jpokebot/user/settingsEggHatch.txt")));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				String[] info = line.split("=");
				if (info.length == 2 && !info[1].equals("")) {
					if (info[0].equals("active"))
						if (info[1].equals("true")) {
							this.isActiveEggHatch = true;
						} else {
							this.isActiveEggHatch = false;
						}
					if (info[0].equals("hatchType"))
						if (info[1].equals("all")) {
							this.isHatchAll = true;
						} else {
							this.isHatchAll = false;
						}
					if (info[0].equals("hatchOnlySelected"))
						this.selectedEggKm = Integer.parseInt(info[1]);
					if (info[0].equals("hatchAllOrderedBy")) {
						if (info[1].equals("descendant")) {
							this.isDescendant = true;
							continue;
						}
						this.isDescendant = false;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double distFromTo(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371.0D;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2.0D) * Math.sin(dLat / 2.0D) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2.0D) * Math.sin(dLng / 2.0D);
		double c = 2.0D * Math.atan2(Math.sqrt(a), Math.sqrt(1.0D - a));
		float dist = (float) (earthRadius * c);

		return dist;
	}

	private void loadPokebank() {
		this.idPokemonLists = new HashMap<Integer, List<Pokemon>>();
		try {
			this.go.getInventories().updateInventories(true);
		} catch (LoginFailedException e) {
			e.printStackTrace();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		}
		this.pokebank = this.go.getInventories().getPokebank().getPokemons();
		for (Pokemon p : this.pokebank) {
			List<Pokemon> newList;
			int id = p.getPokemonId().getNumber();

			if (!this.idPokemonLists.containsKey(Integer.valueOf(id))) {
				newList = new LinkedList<Pokemon>();
			} else {
				newList = (List) this.idPokemonLists.get(Integer.valueOf(id));
			}

			newList.add(p);
			this.idPokemonLists.put(Integer.valueOf(id), newList);
		}
	}

	private static Collection<Pokestop> getLootablePokestop(PokemonGo go, Collection<Pokestop> defaultPokestop) {
		Collection<Pokestop> lootablePokestop = new LinkedList<Pokestop>();
		for (Pokestop pokestop : defaultPokestop) {
			if (pokestop.canLoot(true)) {
				lootablePokestop.add(pokestop);
			}
		}
		return lootablePokestop;
	}

	private void lootPokestop(PokemonGo go, Pokestop ps) throws InterruptedException {
		PokestopLootResult loot = null;
		try {
			loot = ps.loot();
		} catch (LoginFailedException e) {
			e.printStackTrace();
			return;
		} catch (RemoteServerException e) {
			e.printStackTrace();
			return;
		}
		int xp = loot.getExperience();
		List<ItemAwardOuterClass.ItemAward> lootedItems = loot.getItemsAwarded();
		if (xp == 0) {
			this.display.append("Softbanned!\n");
			antiSoftban();
		} else {
			this.display.append("[XP: " + xp + "][ITEMS: ");
			if (lootedItems.size() > 1) {
				HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
				for (ItemAwardOuterClass.ItemAward item : lootedItems) {
					String name = fixItemName(item.getItemId().name());
					if (!itemCount.containsKey(name)) {
						itemCount.put(name, Integer.valueOf(1));
						continue;
					}
					int old = ((Integer) itemCount.get(name)).intValue();
					old++;
					itemCount.put(name, Integer.valueOf(old));
				}

				this.display.append(itemCount + "]\n");
				if (this.isActiveRecycle && !this.isRecycleWhenFull)
					recycleItems();
			} else {
				this.display.append(" Bag full]\n");
				if (this.isActiveRecycle)
					recycleItems();
			}
			try {
				go.getInventories().updateInventories(true);
			} catch (LoginFailedException e1) {
				e1.printStackTrace();
			} catch (RemoteServerException e1) {
				e1.printStackTrace();
			}
			if (this.level != go.getPlayerProfile().getStats().getLevel()) {
				this.display
						.append("LEVEL UP! " + this.level + "?" + go.getPlayerProfile().getStats().getLevel() + "\n");
				this.level = go.getPlayerProfile().getStats().getLevel();
				if (this.isGetLevelReward) {
					List<ItemAwardOuterClass.ItemAward> reward = null;
					try {
						reward = go.getPlayerProfile().acceptLevelUpRewards(this.level).getRewards();
					} catch (RemoteServerException e) {
						e.printStackTrace();
					} catch (LoginFailedException e) {
						e.printStackTrace();
					}
					this.display.append("Reward: ");
					for (ItemAwardOuterClass.ItemAward item : reward) {
						this.display.append(String.valueOf(item.getItemCount()) + "x"
								+ fixItemName(item.getItemId().toString()) + " ");
					}
					this.display.append("\n");
				}
				if (this.isAutoUpdate && this.levelChangeCollect.contains(Integer.valueOf(this.level))) {
					this.frameBot.updateSettings();
					this.display.append("Updated item recycle settings!\n");
				}
			}
		}

		try {
			go.getPlayerProfile().updateProfile();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		} catch (LoginFailedException e) {
			e.printStackTrace();
		}
		long deltaExp = go.getPlayerProfile().getStats().getExperience() - this.initialExp;
		double h = this.time / 3600.0D;
		System.out
				.println("EXP/H=" + (deltaExp / h) + " Time(h)= " + h + " Time(s)= " + this.time + " Exp= " + deltaExp);

		Thread.sleep((this.timeToCollect * 1000));
		this.time += this.timeToCollect;
	}

	private void antiSoftban() {
		try {
			Thread.sleep((this.softbanWaitingTime * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.display.append("Waiting for " + this.softbanWaitingTime + " seconds...\n");
	}

	private void lootBorderPokestop(PokemonGo go) {
		System.out.println("check border");
		PokestopLootResult loot = null;
		Collection<Pokestop> pokestop = null;
		try {
			pokestop = go.getMap().getMapObjects().getPokestops();
		} catch (LoginFailedException e2) {
			e2.printStackTrace();
		} catch (RemoteServerException e2) {
			e2.printStackTrace();
		}
		for (Pokestop p : pokestop) {
			if (!this.defaultPokestop.contains(p) && p.canLoot(true)) {
				this.display.append(this.separator);
				try {
					this.display.append("Extra lootable Pokestop: " + p.getDetails().getName() + "\n");
					loot = p.loot();
				} catch (LoginFailedException e) {
					e.printStackTrace();
					return;
				} catch (RemoteServerException e) {
					e.printStackTrace();
					return;
				}
				this.display.append("Looting...");

				int xp = loot.getExperience();
				List<ItemAwardOuterClass.ItemAward> lootedItems = loot.getItemsAwarded();
				if (xp == 0) {
					this.display.append("Softbanned!\n");
					antiSoftban();
				} else {
					this.display.append("[XP: " + xp + "][ITEMS: ");
					if (lootedItems.size() > 1) {
						HashMap<String, Integer> itemCount = new HashMap<String, Integer>();
						for (ItemAwardOuterClass.ItemAward item : lootedItems) {
							String name = fixItemName(item.getItemId().name());
							if (!itemCount.containsKey(name)) {
								itemCount.put(name, Integer.valueOf(1));
								continue;
							}
							int old = ((Integer) itemCount.get(name)).intValue();
							old++;
							itemCount.put(name, Integer.valueOf(old));
						}

						this.display.append(itemCount + "]\n");
						if (this.isActiveRecycle && !this.isRecycleWhenFull)
							recycleItems();
					} else {
						this.display.append(" Bag full]\n");
						if (this.isActiveRecycle)
							recycleItems();
					}
					try {
						go.getInventories().updateInventories(true);
					} catch (LoginFailedException e1) {
						e1.printStackTrace();
					} catch (RemoteServerException e1) {
						e1.printStackTrace();
					}
					if (this.level != go.getPlayerProfile().getStats().getLevel()) {
						this.display.append(
								"LEVEL UP! " + this.level + "?" + go.getPlayerProfile().getStats().getLevel() + "\n");
						this.level = go.getPlayerProfile().getStats().getLevel();
						if (this.isGetLevelReward) {
							List<ItemAwardOuterClass.ItemAward> reward = null;
							try {
								reward = go.getPlayerProfile().acceptLevelUpRewards(this.level).getRewards();
							} catch (RemoteServerException e) {
								e.printStackTrace();
							} catch (LoginFailedException e) {
								e.printStackTrace();
							}
							this.display.append("Reward: ");
							for (ItemAwardOuterClass.ItemAward item : reward) {
								this.display.append(String.valueOf(item.getItemCount()) + "x"
										+ fixItemName(item.getItemId().toString()) + " ");
							}
							this.display.append("\n");
						}
						if (this.isAutoUpdate && this.levelChangeCollect.contains(Integer.valueOf(this.level))) {
							this.frameBot.updateSettings();
							this.display.append("Updated item recycle settings!\n");
						}
					}
				}
				try {
					Thread.sleep((this.timeToCollect * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.time += this.timeToCollect;
			}
		}
	}

	private void updateInventory() {
		try {
			this.go.getInventories().updateInventories(true);
		} catch (LoginFailedException e) {
			e.printStackTrace();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		}
		Collection<Item> current = this.go.getInventories().getItemBag().getItems();
		for (Item item : current) {
			this.items.put(item.getItemId(), Integer.valueOf(item.getCount()));
		}
	}

	private void recycleItems() {
		try {
			updateInventory();
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_POKE_BALL)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_POKE_BALL)).intValue() > this.kPokeball) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_POKE_BALL)).intValue()
						- this.kPokeball;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_POKE_BALL, drop);
				this.display.append("Dropped " + drop + "x Pokeball\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_GREAT_BALL)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_GREAT_BALL))
							.intValue() > this.kMegaball) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_GREAT_BALL)).intValue()
						- this.kMegaball;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_GREAT_BALL, drop);
				this.display.append("Dropped " + drop + "x Megaball\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_ULTRA_BALL)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_ULTRA_BALL))
							.intValue() > this.kUltraball) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_ULTRA_BALL)).intValue()
						- this.kUltraball;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_ULTRA_BALL, drop);
				this.display.append("Dropped " + drop + "x Ultraball\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_POTION)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_POTION)).intValue() > this.kPotion) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_POTION)).intValue() - this.kPotion;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_POTION, drop);
				this.display.append("Dropped " + drop + "x Potion\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_SUPER_POTION)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_SUPER_POTION))
							.intValue() > this.kSuperPotion) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_SUPER_POTION)).intValue()
						- this.kSuperPotion;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_SUPER_POTION, drop);
				this.display.append("Dropped " + drop + "x Super Potion\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_HYPER_POTION)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_HYPER_POTION))
							.intValue() > this.kHyperPotion) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_HYPER_POTION)).intValue()
						- this.kHyperPotion;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_HYPER_POTION, drop);
				this.display.append("Dropped " + drop + "x Hyper Potion\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_MAX_POTION)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_MAX_POTION))
							.intValue() > this.kMaxPotion) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_MAX_POTION)).intValue()
						- this.kMaxPotion;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_MAX_POTION, drop);
				this.display.append("Dropped " + drop + "x Max Potion\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_REVIVE)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_REVIVE)).intValue() > this.kRevive) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_REVIVE)).intValue() - this.kRevive;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_REVIVE, drop);
				this.display.append("Dropped " + drop + "x Revive\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_MAX_REVIVE)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_MAX_REVIVE))
							.intValue() > this.kMaxRevive) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_MAX_REVIVE)).intValue()
						- this.kMaxRevive;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_MAX_REVIVE, drop);
				this.display.append("Dropped " + drop + "x Max Revive\n");
			}
			if (this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_RAZZ_BERRY)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_RAZZ_BERRY))
							.intValue() > this.kRazzBerry) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_RAZZ_BERRY)).intValue()
						- this.kRazzBerry;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_RAZZ_BERRY, drop);
				this.display.append("Dropped " + drop + "x RazzBerry\n");
			}
			if (!this.isKeepMasterball && this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_MASTER_BALL)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_MASTER_BALL))
							.intValue() > this.kMasterball) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_MASTER_BALL)).intValue()
						- this.kMasterball;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_MASTER_BALL, drop);
				this.display.append("Dropped " + drop + "x Masterball\n");
			}

			if (!this.isKeepLuckyEgg && this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_LUCKY_EGG)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_LUCKY_EGG)).intValue() > this.kLuckyEgg) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_LUCKY_EGG)).intValue()
						- this.kLuckyEgg;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_LUCKY_EGG, drop);
				this.display.append("Dropped " + drop + "x Lucky Egg\n");
			}

			if (!this.isKeepTroyDisk && this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_TROY_DISK)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_TROY_DISK)).intValue() > this.kTroyDisk) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_TROY_DISK)).intValue()
						- this.kTroyDisk;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_TROY_DISK, drop);
				this.display.append("Dropped " + drop + "x Lure Module\n");
			}

			if (!this.isKeepIncense && this.items.containsKey(ItemIdOuterClass.ItemId.ITEM_INCENSE_ORDINARY)
					&& ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_INCENSE_ORDINARY))
							.intValue() > this.kIncense) {
				int drop = ((Integer) this.items.get(ItemIdOuterClass.ItemId.ITEM_INCENSE_ORDINARY)).intValue()
						- this.kIncense;
				this.go.getInventories().getItemBag().removeItem(ItemIdOuterClass.ItemId.ITEM_INCENSE_ORDINARY, drop);
				this.display.append("Dropped " + drop + "x Incense\n");
			}

		} catch (LoginFailedException loginFailedException) {

		} catch (RemoteServerException remoteServerException) {
		}
	}

	private void catchPokemons(List<CatchablePokemon> list) throws InterruptedException {
		for (CatchablePokemon pokemon : list) {
			this.display.append("Trying to catch a "
					+ PokeDictionary.getDisplayName(pokemon.getPokemonId().getNumber(), new Locale("en")) + "... ");
			try {
				this.go.getInventories().updateInventories(true);
			} catch (LoginFailedException e1) {
				e1.printStackTrace();
			} catch (RemoteServerException e1) {
				e1.printStackTrace();
			}
			boolean havePokeball = false;
			for (Item item : this.go.getInventories().getItemBag().getItems()) {
				item.getItemId();
				item.getItemId();
				item.getItemId();
				item.getItemId();
				if ((item.getItemId().equals(ItemIdOuterClass.ItemId.ITEM_POKE_BALL)
						|| item.getItemId().equals(ItemIdOuterClass.ItemId.ITEM_GREAT_BALL)
						|| item.getItemId().equals(ItemIdOuterClass.ItemId.ITEM_ULTRA_BALL)
						|| item.getItemId().equals(ItemIdOuterClass.ItemId.ITEM_MASTER_BALL)) && item.getCount() > 0)
					havePokeball = true;
			}
			if (havePokeball) {
				if (this.isUseBerry)
					if (this.go.getInventories().getItemBag().getItem(ItemIdOuterClass.ItemId.ITEM_RAZZ_BERRY)
							.getCount() <= 0) {
						this.catchOptions.useRazzberries(false);
					} else {
						this.catchOptions.useRazzberries(true);
					}
				if (this.isUseMasterballOnLegendary
						&& this.legendary.contains(Integer.valueOf(pokemon.getPokemonId().getNumber()))) {
					this.catchOptions.noMasterBall(false);
					this.catchOptions.usePokeball(Pokeball.MASTERBALL);
					this.catchOptions.useBestBall(true);
				}
				CatchResult catchResult = null;
				EncounterResult encounterResult = null;
				try {
					encounterResult = pokemon.encounterNormalPokemon();
					if (encounterResult.wasSuccessful() && encounterResult != null) {
						catchResult = pokemon.catchPokemon(this.catchOptions);
					} else {
						this.display.append("encounter failed!\n");
					}
				} catch (LoginFailedException e) {
					e.printStackTrace();
				} catch (RemoteServerException e) {
					e.printStackTrace();
				} catch (NoSuchItemException e) {
					e.printStackTrace();
				} catch (AsyncPokemonGoException e) {
					this.display.append("network error!\n");
					e.printStackTrace();
				}
				if (encounterResult.wasSuccessful())
					if (catchResult != null && !catchResult.isFailed()) {
						this.display.append("caught! [CP " + encounterResult.getPokemonData().getCp() + "]\n");
						if (this.isActiveTransfer) {
							loadPokebank();
							transferPokemon();
							try {
								this.go.getInventories().updateInventories(true);
							} catch (LoginFailedException e) {
								e.printStackTrace();
							} catch (RemoteServerException e) {
								e.printStackTrace();
							}
						}
						if (this.isActiveRename)
							renameAllPokemon();
					} else {
						this.display.append("escaped!\n");
					}
			} else {
				this.display.append("can't catch! No Pokeball!\n");
			}

			this.catchOptions.usePokeball(this.defaultPokeball);
			if (this.pokeballSelection.equals("smart")) {
				this.catchOptions.useSmartSelect(true);
			} else if (this.pokeballSelection.equals("best")) {
				this.catchOptions.useBestBall(true);
			}
			this.catchOptions.noMasterBall(true);

			Thread.sleep((this.timeOutCatch * 1000));
			this.time += this.timeOutCatch;
		}
	}

	private boolean transferPokemon() {
		boolean transfered = false;
		Set<Integer> keys = this.idPokemonLists.keySet();
		for (Integer key : keys) {
			List<Pokemon> currentList = (List) this.idPokemonLists.get(key);
			List<Pokemon> toTransfer = new LinkedList<Pokemon>();
			if (currentList.size() > this.maxDuplicate) {
				for (Pokemon p : currentList) {
					if (p.equals(this.go.getPlayerProfile().getPlayerData().getBuddyPokemon())) {
						continue;
					}
					if (this.isNeverTransferLegendary
							&& this.legendary.contains(Integer.valueOf(p.getPokemonId().getNumber()))) {
						continue;
					}
					if (this.isNeverTransferFavorite && p.isFavorite()) {
						continue;
					}
					if (this.isMinCP && p.getCp() > this.minCP) {
						continue;
					}
					if (this.isMinIV && p.getIvInPercentage() > this.minIV) {
						continue;
					}
					if (this.isActiveSmartTransfer) {
						int cpPercentageMaxPlayerLevel = 100;
						try {
							cpPercentageMaxPlayerLevel = p.getCPInPercentageMaxPlayerLevel();
						} catch (NoSuchItemException e) {
							e.printStackTrace();
						}
						double average = ((cpPercentageMaxPlayerLevel * this.priority)
								+ p.getIvInPercentage() * (100 - this.priority)) / 100.0D;
						if (average > this.minAverage)
							continue;
					}
					if (this.isNeverTransferPerfectMoves
							&& this.fullyEvolved.contains(Integer.valueOf(p.getPokemonId().getNumber()))) {
						String[] perfectMoves = (String[]) this.frameBot.moveset
								.get(Integer.valueOf(p.getPokemonId().getNumber()));
						if (p.getMove1().equals(perfectMoves[0]) && p.getMove2().equals(perfectMoves[1])) {
							continue;
						}
					}
					toTransfer.add(p);
				}
			}
			if (toTransfer.size() <= currentList.size() - this.maxDuplicate) {
				for (Pokemon p : toTransfer) {
					this.display.append(
							"Transfered " + PokeDictionary.getDisplayName(p.getPokemonId().getNumber(), this.locale)
									+ " [CP " + p.getCp() + "][IV " + p.getIvInPercentage() + "%]\n");
					try {
						p.transferPokemon();
					} catch (LoginFailedException e) {
						e.printStackTrace();
					} catch (RemoteServerException e) {
						e.printStackTrace();
					}
					transfered = true;
				}
				continue;
			}
			Collections.sort(toTransfer, (a, b) -> (a.getIvInPercentage() < b.getIvInPercentage()) ? -1 : 1);

			for (int i = 0; i < currentList.size() - this.maxDuplicate; i++) {
				Pokemon p = (Pokemon) toTransfer.get(i);
				this.display
						.append("Transfered " + PokeDictionary.getDisplayName(p.getPokemonId().getNumber(), this.locale)
								+ " [CP " + p.getCp() + "][IV " + p.getIvInPercentage() + "%]\n");
				try {
					p.transferPokemon();
				} catch (LoginFailedException e) {
					e.printStackTrace();
				} catch (RemoteServerException e) {
					e.printStackTrace();
				}
				transfered = true;
			}
		}

		return transfered;
	}

	private void renameAllPokemon() {
		List<Pokemon> list = this.go.getInventories().getPokebank().getPokemons();
		for (int i = 0; i < list.size(); i++) {
			renamePokemon((Pokemon) list.get(i));
		}
	}

	private void renamePokemon(Pokemon pokemon) {
		String name = PokeDictionary.getDisplayName(pokemon.getPokemonId().getNumber(), this.locale);
		String oldNickname = pokemon.getNickname();
		if (oldNickname.equals(""))
			oldNickname = name;
		String newNickname = null;
		boolean pm1 = false;
		boolean pm2 = false;
		String moves = "";
		if (this.isRenamePerfectMoves) {
			String[] perfectMoves = (String[]) this.frameBot.moveset
					.get(Integer.valueOf(pokemon.getPokemonId().getNumber()));
			if (pokemon.getMove1().name().equals(perfectMoves[0]))
				pm1 = true;
			if (pokemon.getMove2().name().equals(perfectMoves[1]))
				pm2 = true;
			if (pm1 && pm2) {
				moves = "�";
			} else if (pm1) {
				moves = "�";
			} else if (pm2) {
				moves = "�";
			}
		}
		if (this.isRenameIV && this.isRenamePerfectMoves) {
			if (name.length() > 9)
				name = name.substring(0, 9);
			newNickname = String.valueOf(name) + (new Double(pokemon.getIvInPercentage())).intValue() + moves;
		} else if (this.isRenameIV) {
			if (name.length() > 10)
				name = name.substring(0, 10);
			newNickname = String.valueOf(name) + (new Double(pokemon.getIvInPercentage())).intValue();
		} else if (this.isRenamePerfectMoves) {
			newNickname = String.valueOf(PokeDictionary.getDisplayName(pokemon.getPokemonId().getNumber(), this.locale))
					+ moves;
		} else {
			newNickname = PokeDictionary.getDisplayName(pokemon.getPokemonId().getNumber(), this.locale);
		}
		if (!oldNickname.equals(newNickname)) {
			try {
				pokemon.renamePokemon(newNickname);
				this.go.getInventories().updateInventories(true);
			} catch (MissingResourceException e) {
				e.printStackTrace();
			} catch (LoginFailedException e) {
				e.printStackTrace();
			} catch (RemoteServerException e) {
				e.printStackTrace();
			}
			this.display.append(String.valueOf(oldNickname) + " renamed to " + newNickname + "\n");
		}
	}

	public String printBasicInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("BASIC INFO & STATS\n");
		sb.append("Username: " + this.go.getPlayerProfile().getPlayerData().getUsername() + "\n");
		String team = this.go.getPlayerProfile().getPlayerData().getTeam().name();
		sb.append("Team: " + team);
		sb.append("\n");
		sb.append("Level: " + this.go.getPlayerProfile().getStats().getLevel() + "\n");
		sb.append("Total XP: " + this.go.getPlayerProfile().getStats().getExperience() + "\n");
		sb.append("XP next level: " + (this.go.getPlayerProfile().getStats().getNextLevelXp()
				- this.go.getPlayerProfile().getStats().getExperience()) + "\n");
		sb.append("Km walked: " + this.go.getPlayerProfile().getStats().getKmWalked() + "\n");
		sb.append("Pokeballs thrown: " + this.go.getPlayerProfile().getStats().getPokeballsThrown() + "\n");
		sb.append("Eggs hatched: " + this.go.getPlayerProfile().getStats().getEggsHatched() + "\n");
		sb.append("Pokestop visited: " + this.go.getPlayerProfile().getStats().getPokeStopVisits() + "\n");
		sb.append("Encountered:" + this.go.getPlayerProfile().getStats().getPokemonsEncountered() + "\n");
		sb.append("Caught:" + this.go.getPlayerProfile().getStats().getPokemonsCaptured() + "\n");
		sb.append("\n");
		sb.append("LOCATION\n");
		sb.append("Latitude: " + this.go.getLatitude() + "\n");
		sb.append("Longitude: " + this.go.getLongitude() + "\n");
		sb.append("Altitude: " + this.go.getAltitude() + "\n");
		sb.append("\n");
		sb.append("POKEDEX\n");
		sb.append("Unique caught:" + this.go.getPlayerProfile().getStats().getUniquePokedexEntries() + "/151\n");

		return sb.toString();
	}

	public String printPokebank(String sortBy) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><table width=\"100%\" border=\"1\">");
		try {
			this.go.getInventories().updateInventories(true);
		} catch (LoginFailedException e) {
			e.printStackTrace();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		} catch (AsyncPokemonGoException e) {
			e.printStackTrace();
		}
		List<Pokemon> pokebank = this.go.getInventories().getPokebank().getPokemons();
		if (sortBy.equals("cp") || sortBy.equals("default")) {
			Collections.sort(pokebank, (a, b) -> (a.getCp() > b.getCp()) ? -1 : 1);
		} else if (sortBy.equals("iv")) {
			Collections.sort(pokebank, (a, b) -> (a.getIvInPercentage() > b.getIvInPercentage()) ? -1 : 1);
		} else if (sortBy.equals("number")) {
			Collections.sort(pokebank,
					(a, b) -> (a.getPokemonId().getNumber() < b.getPokemonId().getNumber()) ? -1 : 1);
		} else if (sortBy.equals("az")) {
			Collections.sort(pokebank, (a, b) -> a.getPokemonId().name().compareTo(b.getPokemonId().name()));
		}
		for (Pokemon pokemon : pokebank) {
			String fixedNumber = String.format("%03d",
					new Object[] { Integer.valueOf(pokemon.getPokemonId().getNumber()) });
			Bot.class.getClassLoader();
			String imgsrc = ClassLoader.getSystemResource("jpokebot/images/pokemon/" + fixedNumber + ".png").toString();
			sb.append("<tr><td>#" + pokemon.getPokemonId().getNumber() + "</td><td><img src=\"" + imgsrc
					+ "\" width=\"10px\" height=\"10px\" /></td><td>"
					+ PokeDictionary.getDisplayName(pokemon.getPokemonId().getNumber(), new Locale("en"))
					+ "</td><td>CP " + pokemon.getCp() + "</td><td>IV " + pokemon.getIvInPercentage()
					+ "%</td><td>Candy " + pokemon.getCandy() + "</td><td>"
					+ pokemon.getMove1().name().replaceAll("_FAST", "").replaceAll("_", " ") + "</td><td>"
					+ pokemon.getMove2().name().replaceAll("_", " ") + "</td></tr>");
		}
		return sb.toString();
	}

	public String printInventory() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><table>");
		List<Item> items = new LinkedList<Item>(this.go.getInventories().getItemBag().getItems());
		Collections.sort(items, (a, b) -> (a.getItemId().getNumber() < b.getItemId().getNumber()) ? -1 : 1);
		for (Item item : items) {
			int itemCount = this.go.getInventories().getItemBag().getItem(item.getItemId()).getCount();
			if (itemCount > 0) {
				String itemName = fixItemName(item.getItemId().name());
				Bot.class.getClassLoader();
				String imgsrc = ClassLoader.getSystemResource("jpokebot/images/items/" + item.getItemId() + ".png")
						.toString();
				sb.append("<tr><td>" + itemCount + "</td><td>x</td><td><img src=\"" + imgsrc
						+ "\" width=\"10px\" height=\"10px\" /></td><td>" + itemName + "</td></tr>");
			}
		}
		sb.append("</table></body></html>");
		return sb.toString();
	}

	public String fixItemName(String name) {
		/*
		 * String fixedName = name.replaceAll("ITEM_", "").toLowerCase(); String[]
		 * nameParts = fixedName.split("_"); StringBuilder sb = new StringBuilder();
		 * byte b; int i; String[] arrayOfString; for (i = arrayOfString =
		 * nameParts.length, b = 0; b < i;) { String s = arrayOfString[b];
		 * sb.append(String.valueOf(s.substring(0, 1).toUpperCase()) + s.substring(1) +
		 * " "); b++; }
		 * 
		 * return sb.toString();
		 */
		return name;
	}
}
