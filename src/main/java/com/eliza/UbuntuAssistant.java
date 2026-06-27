package com.eliza;

import java.util.*;

/**
 * Ubuntu Assistant - Rule-based conversational helper for Linux/Ubuntu
 */
public class UbuntuAssistant {

    private List<Rule> rules;
    private ConversationContext context;
    private Random random;
    private LLMConfig llmConfig;
    private LLMProvider llmProvider;

    public UbuntuAssistant() {
        this.rules = new ArrayList<>();
        this.context = new ConversationContext();
        this.random = new Random();
        this.llmConfig = new LLMConfig();
        this.llmProvider = new LLMProvider(llmConfig);
        initializeRules();
    }

    public String getGreeting() {
        return "Hi! I'm your Ubuntu assistant. I can help you with Linux commands, troubleshooting, software installation, and general questions. What can I help you with?";
    }

    public String getResponse(String input) {
        // Find matching rules sorted by priority
        List<Rule> matchingRules = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.matches(input, context)) {
                matchingRules.add(rule);
            }
        }

        // Sort by priority (higher first)
        matchingRules.sort((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()));

        String response;
        if (!matchingRules.isEmpty()) {
            Rule selectedRule = matchingRules.get(0);
            response = selectedRule.getResponse(context);
            selectedRule.executeActions(context);

            // Update context
            if (selectedRule.getTopic() != null) {
                context.setCurrentTopic(selectedRule.getTopic());
            }
        } else {
            // Try LLM fallback if enabled
            if (llmConfig.isLLMEnabled()) {
                String llmResponse = llmProvider.query(input);
                if (llmResponse != null && !llmResponse.isEmpty()) {
                    response = "🤖 " + llmResponse;
                } else {
                    response = getDefaultResponse();
                }
            } else {
                response = getDefaultResponse();
            }
        }

        // Store in history
        context.addToHistory(input, response);

        return response;
    }

    private String getDefaultResponse() {
        String[] defaults = {
            "I'm not sure I understand. Can you rephrase that?",
            "Could you be more specific?",
            "What exactly do you need help with?",
            "Can you provide more details?",
            "I'm here to help! What's your question?",
            "Tell me more about what you're trying to do.",
            "What command or task are you working on?",
            "Is this related to a specific error or issue?",
            "What were you trying to accomplish?",
            "Let me know more details and I'll do my best to help!"
        };
        return defaults[random.nextInt(defaults.length)];
    }

    private void initializeRules() {
        initializeGreetingRules();
        initializeCommandHelpRules();
        initializeFileSystemRules();
        initializePackageManagementRules();
        initializeSystemInfoRules();
        initializeNetworkingRules();
        initializeTroubleshootingRules();
        initializeSoftwareRules();
        initializeGeneralConversationRules();

        // Configuration topics - comprehensive Linux/Ubuntu knowledge
        initializeDesktopEnvironmentRules();
        initializeDisplayManagerRules();
        initializeAudioRules();
        initializeGraphicsDriverRules();
        initializeSystemdRules();
        initializeBootGrubRules();
        initializeDisplayRules();
        initializePrinterScannerRules();
        initializeBluetoothRules();
        initializeUserPermissionRules();
    }

    private void initializeGreetingRules() {
        // HELLO/HI
        Rule hello = new Rule("hello", 95);
        hello.addPattern("HELLO");
        hello.addPattern("HI ");
        hello.addPattern("HEY");
        hello.addPattern("GREETINGS");
        hello.addPattern("GOOD MORNING");
        hello.addPattern("GOOD AFTERNOON");
        hello.addPattern("GOOD EVENING");
        hello.addResponse("Hello! How can I assist you with Ubuntu today?");
        hello.addResponse("Hi there! What would you like help with?");
        hello.addResponse("Hey! I'm here to help with any Ubuntu questions.");
        hello.addResponse("Greetings! What brings you here?");
        hello.addResponse("Hello! Need help with a command or system issue?");
        hello.setTopic("greeting");
        rules.add(hello);

        // THANK YOU
        Rule thanks = new Rule("thanks", 90);
        thanks.addPattern("THANK");
        thanks.addPattern("THANKS");
        thanks.addPattern("APPRECIATE");
        thanks.addResponse("You're welcome!");
        thanks.addResponse("Happy to help!");
        thanks.addResponse("No problem! Let me know if you need anything else.");
        thanks.addResponse("Glad I could assist!");
        thanks.addResponse("Anytime!");
        thanks.setTopic("gratitude");
        rules.add(thanks);

        // GOODBYE
        Rule goodbye = new Rule("goodbye", 90);
        goodbye.addPattern("BYE");
        goodbye.addPattern("GOODBYE");
        goodbye.addPattern("SEE YOU");
        goodbye.addPattern("LATER");
        goodbye.addResponse("Goodbye! Feel free to come back anytime.");
        goodbye.addResponse("See you! Good luck with your Ubuntu setup.");
        goodbye.addResponse("Take care! Let me know if you need more help.");
        goodbye.addResponse("Bye! Happy computing!");
        goodbye.setTopic("farewell");
        rules.add(goodbye);
    }

    private void initializeCommandHelpRules() {
        // LS COMMAND
        Rule ls = new Rule("ls", 85);
        ls.addPattern("LS ");
        ls.addPattern(" LS");
        ls.addPattern("LIST FILES");
        ls.addPattern("SHOW FILES");
        ls.addResponse("The 'ls' command lists files and directories. Use 'ls -la' to show all files including hidden ones with details.");
        ls.addResponse("To list files: 'ls' for basic listing, 'ls -l' for detailed view, 'ls -a' to include hidden files.");
        ls.addResponse("Try 'ls -lh' to see file sizes in human-readable format!");
        ls.setTopic("commands");
        rules.add(ls);

        // CD COMMAND
        Rule cd = new Rule("cd", 85);
        cd.addPattern("CD ");
        cd.addPattern(" CD");
        cd.addPattern("CHANGE DIRECTORY");
        cd.addPattern("NAVIGATE");
        cd.addResponse("Use 'cd' to change directories. Example: 'cd /home/user/Documents'");
        cd.addResponse("'cd ..' goes up one directory, 'cd ~' goes to your home directory.");
        cd.addResponse("To change directory: 'cd [path]'. Use 'pwd' to see your current location.");
        cd.setTopic("commands");
        rules.add(cd);

        // MKDIR COMMAND
        Rule mkdir = new Rule("mkdir", 85);
        mkdir.addPattern("MKDIR");
        mkdir.addPattern("CREATE DIRECTORY");
        mkdir.addPattern("MAKE FOLDER");
        mkdir.addPattern("NEW FOLDER");
        mkdir.addResponse("Create a directory with 'mkdir foldername'. Use 'mkdir -p path/to/nested/folders' for nested directories.");
        mkdir.addResponse("The 'mkdir' command creates new directories. Example: 'mkdir MyFolder'");
        mkdir.addResponse("To create multiple directories: 'mkdir folder1 folder2 folder3'");
        mkdir.setTopic("commands");
        rules.add(mkdir);

        // RM COMMAND
        Rule rm = new Rule("rm", 90);
        rm.addPattern("RM ");
        rm.addPattern(" RM");
        rm.addPattern("DELETE FILE");
        rm.addPattern("REMOVE FILE");
        rm.addResponse("⚠️ Be careful with 'rm'! It permanently deletes files. Use 'rm filename' to delete a file, 'rm -r folder' to delete a directory.");
        rm.addResponse("To delete files: 'rm file.txt'. For directories: 'rm -r foldername'. WARNING: This is permanent!");
        rm.addResponse("CAUTION: 'rm -rf' deletes without confirmation. Use 'rm -i' for interactive mode to confirm each deletion.");
        rm.setTopic("commands");
        rules.add(rm);

        // CP COMMAND
        Rule cp = new Rule("cp", 85);
        cp.addPattern("CP ");
        cp.addPattern("COPY FILE");
        cp.addPattern("COPY");
        cp.addResponse("Copy files with 'cp source destination'. Example: 'cp file.txt /home/user/backup/'");
        cp.addResponse("To copy a directory: 'cp -r source_folder destination_folder'");
        cp.addResponse("Use 'cp -i' to prompt before overwriting existing files.");
        cp.setTopic("commands");
        rules.add(cp);

        // MV COMMAND
        Rule mv = new Rule("mv", 85);
        mv.addPattern("MV ");
        mv.addPattern("MOVE FILE");
        mv.addPattern("RENAME");
        mv.addResponse("Move or rename files with 'mv'. Example: 'mv oldname.txt newname.txt' or 'mv file.txt /new/location/'");
        mv.addResponse("'mv' moves files and can also rename them. Syntax: 'mv source destination'");
        mv.addResponse("To rename: 'mv old_name new_name'. To move: 'mv file /destination/path/'");
        mv.setTopic("commands");
        rules.add(mv);

        // CHMOD COMMAND
        Rule chmod = new Rule("chmod", 85);
        chmod.addPattern("CHMOD");
        chmod.addPattern("PERMISSIONS");
        chmod.addPattern("FILE PERMISSIONS");
        chmod.addPattern("CHANGE PERMISSIONS");
        chmod.addResponse("'chmod' changes file permissions. Example: 'chmod 755 file.sh' or 'chmod +x script.sh' to make it executable.");
        chmod.addResponse("Common permissions: 755 (rwxr-xr-x), 644 (rw-r--r--), 777 (rwxrwxrwx - be careful!)");
        chmod.addResponse("Use 'chmod +x filename' to make a file executable, 'chmod -R' for recursive directory changes.");
        chmod.setTopic("commands");
        rules.add(chmod);

        // GREP COMMAND
        Rule grep = new Rule("grep", 85);
        grep.addPattern("GREP");
        grep.addPattern("SEARCH FILE");
        grep.addPattern("FIND TEXT");
        grep.addResponse("'grep' searches for text in files. Example: 'grep \"search term\" filename.txt'");
        grep.addResponse("Search all files in a directory: 'grep -r \"pattern\" /path/to/directory'");
        grep.addResponse("Use 'grep -i' for case-insensitive search, 'grep -n' to show line numbers.");
        grep.setTopic("commands");
        rules.add(grep);

        // SUDO COMMAND
        Rule sudo = new Rule("sudo", 90);
        sudo.addPattern("SUDO");
        sudo.addPattern("ROOT");
        sudo.addPattern("ADMINISTRATOR");
        sudo.addPattern("SUPERUSER");
        sudo.addResponse("'sudo' runs commands as root/administrator. Be careful! Example: 'sudo apt update'");
        sudo.addResponse("With great power comes great responsibility! 'sudo' gives you admin privileges.");
        sudo.addResponse("Use 'sudo' before commands that require administrator access. You'll be prompted for your password.");
        sudo.setTopic("commands");
        rules.add(sudo);

        // TAR COMMAND
        Rule tar = new Rule("tar", 85);
        tar.addPattern("TAR");
        tar.addPattern("ARCHIVE");
        tar.addPattern("COMPRESS");
        tar.addPattern("EXTRACT");
        tar.addResponse("Create archive: 'tar -czf archive.tar.gz folder/', Extract: 'tar -xzf archive.tar.gz'");
        tar.addResponse("'tar' creates and extracts archives. -c=create, -x=extract, -z=gzip, -f=file");
        tar.addResponse("Common tar commands: Create: 'tar -czf output.tar.gz input/', Extract: 'tar -xzf archive.tar.gz'");
        tar.setTopic("commands");
        rules.add(tar);
    }

    private void initializeFileSystemRules() {
        // DISK SPACE
        Rule diskSpace = new Rule("disk-space", 85);
        diskSpace.addPattern("DISK SPACE");
        diskSpace.addPattern("STORAGE");
        diskSpace.addPattern("FREE SPACE");
        diskSpace.addPattern("HOW MUCH SPACE");
        diskSpace.addResponse("Check disk space with 'df -h' for human-readable output.");
        diskSpace.addResponse("Use 'du -sh *' to see sizes of files/folders in current directory.");
        diskSpace.addResponse("'ncdu' is a nice interactive disk usage viewer if you have it installed.");
        diskSpace.setTopic("filesystem");
        rules.add(diskSpace);

        // HOME DIRECTORY
        Rule home = new Rule("home", 80);
        home.addPattern("HOME DIRECTORY");
        home.addPattern("HOME FOLDER");
        home.addPattern("USER FOLDER");
        home.addResponse("Your home directory is at /home/username. Use 'cd ~' or just 'cd' to go there.");
        home.addResponse("The tilde '~' is a shortcut for your home directory. Example: 'cd ~/Documents'");
        home.addResponse("Your personal files are stored in /home/yourusername");
        home.setTopic("filesystem");
        rules.add(home);

        // FILE LOCATION
        Rule find = new Rule("find", 85);
        find.addPattern("FIND FILE");
        find.addPattern("LOCATE FILE");
        find.addPattern("WHERE IS");
        find.addPattern("SEARCH FOR FILE");
        find.addResponse("Find files with 'find /path -name \"filename\"'. Example: 'find ~ -name \"*.txt\"'");
        find.addResponse("Use 'locate filename' for faster searches (requires updatedb to be run first).");
        find.addResponse("'find' command: 'find /starting/path -name \"pattern\"' or use 'locate' for quicker searches.");
        find.setTopic("filesystem");
        rules.add(find);
    }

    private void initializePackageManagementRules() {
        // APT UPDATE/UPGRADE
        Rule aptUpdate = new Rule("apt-update", 90);
        aptUpdate.addPattern("UPDATE");
        aptUpdate.addPattern("UPGRADE");
        aptUpdate.addPattern("APT UPDATE");
        aptUpdate.addPattern("APT UPGRADE");
        aptUpdate.addResponse("Update package lists: 'sudo apt update', then upgrade: 'sudo apt upgrade'");
        aptUpdate.addResponse("Keep your system updated with 'sudo apt update && sudo apt upgrade'");
        aptUpdate.addResponse("'apt update' refreshes package lists, 'apt upgrade' installs available updates.");
        aptUpdate.setTopic("packages");
        aptUpdate.addAction(ctx -> ctx.setVariable("discussed_updates", true));
        rules.add(aptUpdate);

        // INSTALL SOFTWARE
        Rule aptInstall = new Rule("apt-install", 90);
        aptInstall.addPattern("INSTALL");
        aptInstall.addPattern("APT INSTALL");
        aptInstall.addPattern("HOW TO INSTALL");
        aptInstall.addResponse("Install software with 'sudo apt install packagename'. Example: 'sudo apt install firefox'");
        aptInstall.addResponse("To install: 'sudo apt install [package]'. Search for packages: 'apt search [name]'");
        aptInstall.addResponse("Use 'sudo apt install' to install software from Ubuntu repositories.");
        aptInstall.setTopic("packages");
        rules.add(aptInstall);

        // REMOVE SOFTWARE
        Rule aptRemove = new Rule("apt-remove", 85);
        aptRemove.addPattern("REMOVE");
        aptRemove.addPattern("UNINSTALL");
        aptRemove.addPattern("DELETE PROGRAM");
        aptRemove.addResponse("Remove software: 'sudo apt remove packagename' or 'sudo apt purge packagename' to also delete config files.");
        aptRemove.addResponse("Uninstall with 'sudo apt remove [package]'. Use 'purge' instead of 'remove' to delete configuration files too.");
        aptRemove.addResponse("'apt remove' uninstalls but keeps configs. 'apt purge' removes everything.");
        aptRemove.setTopic("packages");
        rules.add(aptRemove);

        // SNAP
        Rule snap = new Rule("snap", 85);
        snap.addPattern("SNAP");
        snap.addPattern("SNAP INSTALL");
        snap.addPattern("SNAPCRAFT");
        snap.addResponse("Snap is Ubuntu's universal package format. Install with 'sudo snap install packagename'");
        snap.addResponse("List snaps: 'snap list', Install: 'sudo snap install [app]', Remove: 'sudo snap remove [app]'");
        snap.addResponse("Snaps are self-contained packages. Use 'snap find [name]' to search.");
        snap.setTopic("packages");
        rules.add(snap);

        // PPA
        Rule ppa = new Rule("ppa", 85);
        ppa.addPattern("PPA");
        ppa.addPattern("ADD PPA");
        ppa.addPattern("REPOSITORY");
        ppa.addResponse("Add a PPA: 'sudo add-apt-repository ppa:name/ppa', then 'sudo apt update'");
        ppa.addResponse("PPAs are third-party repositories. Add with 'sudo add-apt-repository', remove with 'sudo add-apt-repository --remove'");
        ppa.addResponse("Be careful with PPAs! Only add from trusted sources.");
        ppa.setTopic("packages");
        rules.add(ppa);
    }

    private void initializeSystemInfoRules() {
        // SYSTEM INFO
        Rule sysInfo = new Rule("system-info", 85);
        sysInfo.addPattern("SYSTEM INFO");
        sysInfo.addPattern("COMPUTER INFO");
        sysInfo.addPattern("HARDWARE");
        sysInfo.addPattern("SPECS");
        sysInfo.addResponse("View system info with 'lscpu' for CPU, 'free -h' for RAM, 'lsblk' for disks.");
        sysInfo.addResponse("Get detailed hardware info: 'sudo lshw' or 'inxi -Fxz'");
        sysInfo.addResponse("Quick system overview: 'neofetch' (install it if needed)");
        sysInfo.setTopic("system");
        rules.add(sysInfo);

        // PROCESSES
        Rule processes = new Rule("processes", 85);
        processes.addPattern("PROCESSES");
        processes.addPattern("RUNNING PROGRAMS");
        processes.addPattern("TASK MANAGER");
        processes.addPattern("CPU USAGE");
        processes.addResponse("View processes with 'top' or 'htop' (htop is nicer but needs to be installed)");
        processes.addResponse("'ps aux' shows all running processes. 'kill PID' terminates a process.");
        processes.addResponse("System monitor: 'gnome-system-monitor' for GUI or 'htop' for terminal.");
        processes.setTopic("system");
        rules.add(processes);

        // MEMORY
        Rule memory = new Rule("memory", 85);
        memory.addPattern("MEMORY");
        memory.addPattern("RAM");
        memory.addPattern("MEMORY USAGE");
        memory.addResponse("Check memory usage with 'free -h' for human-readable output.");
        memory.addResponse("See detailed memory info: 'cat /proc/meminfo' or 'free -m'");
        memory.addResponse("'free -h' shows total, used, and available RAM.");
        memory.setTopic("system");
        rules.add(memory);

        // VERSION
        Rule version = new Rule("version", 85);
        version.addPattern("VERSION");
        version.addPattern("UBUNTU VERSION");
        version.addPattern("WHAT VERSION");
        version.addResponse("Check Ubuntu version: 'lsb_release -a' or 'cat /etc/os-release'");
        version.addResponse("'lsb_release -a' shows your Ubuntu version and codename.");
        version.addResponse("Quick version check: 'lsb_release -d'");
        version.setTopic("system");
        rules.add(version);
    }

    private void initializeNetworkingRules() {
        // WIFI
        Rule wifi = new Rule("wifi", 85);
        wifi.addPattern("WIFI");
        wifi.addPattern("WIRELESS");
        wifi.addPattern("WI-FI");
        wifi.addPattern("CONNECT TO WIFI");
        wifi.addResponse("Manage WiFi with 'nmcli' or the network manager GUI in the system tray.");
        wifi.addResponse("List WiFi networks: 'nmcli dev wifi list', Connect: 'nmcli dev wifi connect SSID password PASSWORD'");
        wifi.addResponse("Use 'iwconfig' to see wireless interface details.");
        wifi.setTopic("network");
        rules.add(wifi);

        // IP ADDRESS
        Rule ip = new Rule("ip", 85);
        ip.addPattern("IP ADDRESS");
        ip.addPattern("MY IP");
        ip.addPattern("NETWORK ADDRESS");
        ip.addResponse("Check your IP address with 'ip addr' or 'hostname -I'");
        ip.addResponse("'ip addr show' displays all network interfaces and their IPs.");
        ip.addResponse("Public IP: 'curl ifconfig.me', Local IP: 'hostname -I'");
        ip.setTopic("network");
        rules.add(ip);

        // SSH
        Rule ssh = new Rule("ssh", 85);
        ssh.addPattern("SSH");
        ssh.addPattern("REMOTE ACCESS");
        ssh.addPattern("CONNECT REMOTELY");
        ssh.addResponse("Connect via SSH: 'ssh username@hostname'. Install SSH server: 'sudo apt install openssh-server'");
        ssh.addResponse("SSH connects to remote machines: 'ssh user@192.168.1.100'");
        ssh.addResponse("Generate SSH keys: 'ssh-keygen', Copy keys: 'ssh-copy-id user@host'");
        ssh.setTopic("network");
        rules.add(ssh);

        // FIREWALL
        Rule firewall = new Rule("firewall", 85);
        firewall.addPattern("FIREWALL");
        firewall.addPattern("UFW");
        firewall.addPattern("SECURITY");
        firewall.addResponse("Ubuntu uses UFW firewall. Enable: 'sudo ufw enable', Status: 'sudo ufw status'");
        firewall.addResponse("Allow a port: 'sudo ufw allow 22', Deny: 'sudo ufw deny 80'");
        firewall.addResponse("UFW (Uncomplicated Firewall) manages network security. Check status: 'sudo ufw status verbose'");
        firewall.setTopic("network");
        rules.add(firewall);
    }

    private void initializeTroubleshootingRules() {
        // ERROR
        Rule error = new Rule("error", 90);
        error.addPattern("ERROR");
        error.addPattern("NOT WORKING");
        error.addPattern("PROBLEM");
        error.addPattern("ISSUE");
        error.addResponse("What error are you seeing? Please share the exact error message.");
        error.addResponse("Can you describe the problem in more detail? What were you trying to do?");
        error.addResponse("Check system logs with 'journalctl -xe' or 'dmesg' for recent errors.");
        error.setTopic("troubleshooting");
        rules.add(error);

        // SLOW SYSTEM
        Rule slow = new Rule("slow", 85);
        slow.addPattern("SLOW");
        slow.addPattern("SLUGGISH");
        slow.addPattern("LAG");
        slow.addPattern("PERFORMANCE");
        slow.addResponse("Check resource usage with 'top' or 'htop'. High CPU/RAM usage? Look for problematic processes.");
        slow.addResponse("System slow? Try: 1) Close unused programs, 2) Check disk space with 'df -h', 3) Look at startup applications.");
        slow.addResponse("Performance issues often relate to disk space, RAM, or background processes. Check 'htop' for resource hogs.");
        slow.setTopic("troubleshooting");
        rules.add(slow);

        // LOGS
        Rule logs = new Rule("logs", 85);
        logs.addPattern("LOGS");
        logs.addPattern("LOG FILES");
        logs.addPattern("CHECK LOGS");
        logs.addResponse("View logs with 'journalctl' for systemd logs or check /var/log/ for specific service logs.");
        logs.addResponse("Recent system logs: 'journalctl -xe', Kernel logs: 'dmesg', Application logs: /var/log/");
        logs.addResponse("'tail -f /var/log/syslog' shows live system log updates.");
        logs.setTopic("troubleshooting");
        rules.add(logs);

        // BOOT ISSUES
        Rule boot = new Rule("boot", 90);
        boot.addPattern("BOOT");
        boot.addPattern("WON'T BOOT");
        boot.addPattern("STARTUP");
        boot.addPattern("GRUB");
        boot.addResponse("Boot issues? Try booting into recovery mode from GRUB menu (hold Shift at startup).");
        boot.addResponse("GRUB problems? Reinstall with 'sudo update-grub' from recovery mode or live USB.");
        boot.addResponse("Can't boot? Boot from live USB, mount your drive, and run 'sudo update-grub' or 'sudo grub-install'");
        boot.setTopic("troubleshooting");
        rules.add(boot);
    }

    private void initializeSoftwareRules() {
        // BROWSER
        Rule browser = new Rule("browser", 80);
        browser.addPattern("BROWSER");
        browser.addPattern("FIREFOX");
        browser.addPattern("CHROME");
        browser.addPattern("WEB BROWSER");
        browser.addResponse("Ubuntu comes with Firefox. Install Chrome: Download .deb from google.com/chrome then 'sudo dpkg -i chrome.deb'");
        browser.addResponse("Browsers available: Firefox (default), Chromium: 'sudo apt install chromium-browser', Chrome (download from Google)");
        browser.addResponse("Firefox is pre-installed. Want Chrome? Download the .deb and install with 'sudo dpkg -i'");
        browser.setTopic("software");
        rules.add(browser);

        // TEXT EDITOR
        Rule editor = new Rule("editor", 80);
        editor.addPattern("TEXT EDITOR");
        editor.addPattern("EDITOR");
        editor.addPattern("NOTEPAD");
        editor.addPattern("EDIT FILES");
        editor.addResponse("Text editors: nano (simple), vim (advanced), gedit (GUI). Install VS Code: 'sudo snap install code --classic'");
        editor.addResponse("For beginners: 'nano filename' is easiest. For GUI: 'gedit' or install VS Code.");
        editor.addResponse("Command line editors: nano, vim, emacs. GUI: gedit, VS Code, Sublime Text.");
        editor.setTopic("software");
        rules.add(editor);

        // OFFICE
        Rule office = new Rule("office", 80);
        office.addPattern("OFFICE");
        office.addPattern("WORD");
        office.addPattern("EXCEL");
        office.addPattern("LIBREOFFICE");
        office.addResponse("LibreOffice is pre-installed for documents/spreadsheets. It's compatible with Microsoft Office formats.");
        office.addResponse("Office suite: LibreOffice (free, pre-installed) or install OnlyOffice: 'sudo snap install onlyoffice-desktopeditors'");
        office.addResponse("Open Office documents with LibreOffice Writer, Calc, and Impress (pre-installed).");
        office.setTopic("software");
        rules.add(office);
    }

    private void initializeGeneralConversationRules() {
        // HELP
        Rule help = new Rule("help", 85);
        help.addPattern("HELP");
        help.addPattern("HOW DO I");
        help.addPattern("CAN YOU");
        help.addPattern("ASSIST");
        help.addResponse("I'm here to help! What do you need assistance with?");
        help.addResponse("Tell me what you're trying to do and I'll guide you.");
        help.addResponse("What would you like help with? Commands, software, troubleshooting?");
        help.setTopic("help");
        rules.add(help);

        // WHAT IS
        Rule whatIs = new Rule("what-is", 80);
        whatIs.addPattern("WHAT IS");
        whatIs.addPattern("WHAT ARE");
        whatIs.addPattern("EXPLAIN");
        whatIs.addResponse("What would you like to know about?");
        whatIs.addResponse("I can explain Linux commands, concepts, or Ubuntu features. What specifically?");
        whatIs.addResponse("Ask me about any Ubuntu/Linux topic and I'll explain it!");
        whatIs.setTopic("questions");
        rules.add(whatIs);

        // UBUNTU
        Rule ubuntu = new Rule("ubuntu", 80);
        ubuntu.addPattern("UBUNTU");
        ubuntu.addPattern("WHAT IS UBUNTU");
        ubuntu.addPattern("TELL ME ABOUT UBUNTU");
        ubuntu.addResponse("Ubuntu is a popular Linux distribution based on Debian. It's free, open-source, and user-friendly!");
        ubuntu.addResponse("Ubuntu is one of the most popular Linux operating systems, known for being beginner-friendly and stable.");
        ubuntu.addResponse("Ubuntu: A free Linux OS with great community support, released every 6 months with LTS versions every 2 years.");
        ubuntu.setTopic("ubuntu");
        rules.add(ubuntu);

        // LINUX
        Rule linux = new Rule("linux", 80);
        linux.addPattern("LINUX");
        linux.addPattern("WHAT IS LINUX");
        linux.addResponse("Linux is a free, open-source operating system kernel. Ubuntu is a Linux distribution (distro).");
        linux.addResponse("Linux powers everything from servers to smartphones. Ubuntu is one of many Linux distributions.");
        linux.addResponse("Linux is an OS kernel created by Linus Torvalds. It's the base for many operating systems including Ubuntu.");
        linux.setTopic("linux");
        rules.add(linux);
    }

    // ===================================================================
    // DESKTOP ENVIRONMENTS
    // ===================================================================

    private void initializeDesktopEnvironmentRules() {
        // GNOME
        Rule gnome = new Rule("gnome", 85);
        gnome.addPattern("GNOME");
        gnome.addPattern("GNOME DESKTOP");
        gnome.addPattern("GNOME SHELL");
        gnome.addResponse("GNOME is Ubuntu's default desktop. Install: 'sudo apt install ubuntu-desktop'");
        gnome.addResponse("GNOME uses Wayland by default on Ubuntu. Switch to X11 at login screen (click gear icon).");
        gnome.addResponse("Customize GNOME with GNOME Tweaks: 'sudo apt install gnome-tweaks'");
        gnome.addResponse("GNOME extensions add functionality. Install: 'sudo apt install gnome-shell-extensions'");
        gnome.addResponse("GNOME version check: 'gnome-shell --version'");
        gnome.addResponse("Restart GNOME shell: Alt+F2, type 'r', press Enter (X11 only)");
        gnome.addResponse("GNOME settings: Open 'Settings' from Activities or 'gnome-control-center' in terminal");
        gnome.addResponse("GNOME uses less RAM than you think - about 700-800MB idle with modern versions.");
        gnome.setTopic("desktop");
        rules.add(gnome);

        // KDE PLASMA
        Rule kde = new Rule("kde", 85);
        kde.addPattern("KDE");
        kde.addPattern("PLASMA");
        kde.addPattern("KDE PLASMA");
        kde.addResponse("Install KDE Plasma: 'sudo apt install kubuntu-desktop'");
        kde.addResponse("KDE Plasma is highly customizable. Full installation: 'sudo apt install kde-full'");
        kde.addResponse("Switch to KDE: Install, then select 'Plasma' at login screen before entering password.");
        kde.addResponse("KDE settings: Open 'System Settings' or run 'systemsettings5'");
        kde.addResponse("KDE uses Kwin compositor. Check version: 'kwin_x11 --version' or 'kwin_wayland --version'");
        kde.addResponse("KDE widgets (plasmoids): Right-click desktop → Add Widgets");
        kde.addResponse("KDE Plasma 5 works on both X11 and Wayland. Select at login.");
        kde.addResponse("Uninstall KDE: 'sudo apt remove kubuntu-desktop kde-plasma-desktop'");
        kde.addResponse("KDE uses about 500-600MB RAM idle - lighter than GNOME!");
        kde.setTopic("desktop");
        rules.add(kde);

        // XFCE
        Rule xfce = new Rule("xfce", 85);
        xfce.addPattern("XFCE");
        xfce.addPattern("XFCE4");
        xfce.addPattern("XUBUNTU");
        xfce.addResponse("Install XFCE: 'sudo apt install xubuntu-desktop'");
        xfce.addResponse("XFCE is lightweight and fast - perfect for older hardware!");
        xfce.addResponse("XFCE settings: Run 'xfce4-settings-manager' or find in Applications menu");
        xfce.addResponse("XFCE uses about 300-400MB RAM - very lightweight.");
        xfce.addResponse("Switch to XFCE: Select 'Xfce Session' at login screen");
        xfce.addResponse("XFCE compositor: Enable in Settings → Window Manager Tweaks → Compositor");
        xfce.addResponse("XFCE panels: Right-click panel → Panel → Panel Preferences");
        xfce.addResponse("XFCE file manager is Thunar. Settings: Edit → Preferences");
        xfce.setTopic("desktop");
        rules.add(xfce);

        // CINNAMON
        Rule cinnamon = new Rule("cinnamon", 85);
        cinnamon.addPattern("CINNAMON");
        cinnamon.addPattern("LINUX MINT");
        cinnamon.addResponse("Install Cinnamon: 'sudo apt install cinnamon-desktop-environment'");
        cinnamon.addResponse("Cinnamon is Linux Mint's desktop - traditional Windows-like layout.");
        cinnamon.addResponse("Switch to Cinnamon: Select 'Cinnamon' at login screen");
        cinnamon.addResponse("Cinnamon settings: Run 'cinnamon-settings' or find in menu");
        cinnamon.addResponse("Cinnamon themes and applets: Right-click panel → Applets/Themes");
        cinnamon.addResponse("Cinnamon uses about 500-600MB RAM idle.");
        cinnamon.addResponse("Restart Cinnamon: Ctrl+Alt+Esc or 'cinnamon --replace' in terminal");
        cinnamon.setTopic("desktop");
        rules.add(cinnamon);

        // MATE
        Rule mate = new Rule("mate", 85);
        mate.addPattern("MATE");
        mate.addPattern("MATE DESKTOP");
        mate.addResponse("Install MATE: 'sudo apt install ubuntu-mate-desktop'");
        mate.addResponse("MATE is a continuation of GNOME 2 - traditional desktop layout.");
        mate.addResponse("MATE is lightweight - about 300-400MB RAM usage.");
        mate.addResponse("Switch to MATE: Select 'MATE' at login screen");
        mate.addResponse("MATE settings: 'mate-control-center' or System menu");
        mate.addResponse("MATE uses Caja file manager and Pluma text editor.");
        mate.addResponse("MATE panels: Right-click panel → Properties to customize");
        mate.setTopic("desktop");
        rules.add(mate);

        // LXQT
        Rule lxqt = new Rule("lxqt", 85);
        lxqt.addPattern("LXQT");
        lxqt.addPattern("LXQt");
        lxqt.addPattern("LUBUNTU");
        lxqt.addResponse("Install LXQt: 'sudo apt install lubuntu-desktop'");
        lxqt.addResponse("LXQt is extremely lightweight - about 250-350MB RAM!");
        lxqt.addResponse("LXQt is the Qt-based successor to LXDE - great for old hardware.");
        lxqt.addResponse("Switch to LXQt: Select 'LXQt' at login screen");
        lxqt.addResponse("LXQt settings: Run 'lxqt-config' or find in menu");
        lxqt.addResponse("LXQt file manager is PCManFM-Qt - fast and simple.");
        lxqt.addResponse("LXQt uses Openbox window manager by default.");
        lxqt.setTopic("desktop");
        rules.add(lxqt);

        // BUDGIE
        Rule budgie = new Rule("budgie", 85);
        budgie.addPattern("BUDGIE");
        budgie.addPattern("BUDGIE DESKTOP");
        budgie.addResponse("Install Budgie: 'sudo apt install ubuntu-budgie-desktop'");
        budgie.addResponse("Budgie is modern and elegant - developed by Solus project.");
        budgie.addResponse("Budgie uses about 500MB RAM - moderate resource usage.");
        budgie.addResponse("Switch to Budgie: Select 'Budgie Desktop' at login screen");
        budgie.addResponse("Budgie settings: Open 'Budgie Desktop Settings' or right-click panel");
        budgie.addResponse("Budgie sidebar (Raven): Click clock/date or press Super+A");
        budgie.addResponse("Budgie applets: Right-click panel → Budgie Desktop Settings → Applets");
        budgie.setTopic("desktop");
        rules.add(budgie);

        // DESKTOP ENVIRONMENT GENERAL
        Rule desktopSwitch = new Rule("desktop-switch", 90);
        desktopSwitch.addPattern("SWITCH DESKTOP");
        desktopSwitch.addPattern("CHANGE DESKTOP");
        desktopSwitch.addPattern("DESKTOP ENVIRONMENT");
        desktopSwitch.addResponse("Switch desktop environments at login screen - click gear/settings icon before logging in.");
        desktopSwitch.addResponse("Install multiple desktops and choose at login: GNOME, KDE, XFCE, etc.");
        desktopSwitch.addResponse("Desktop environment vs Window Manager: DE includes full suite (file manager, settings, etc), WM just manages windows.");
        desktopSwitch.addResponse("Popular DEs: GNOME (default), KDE (customizable), XFCE (lightweight), Cinnamon (traditional)");
        desktopSwitch.addResponse("Remove unwanted desktop: 'sudo apt remove [desktop-name]-desktop' but be careful with dependencies!");
        desktopSwitch.setTopic("desktop");
        rules.add(desktopSwitch);

        // WAYLAND VS X11
        Rule waylandX11 = new Rule("wayland-x11", 90);
        waylandX11.addPattern("WAYLAND");
        waylandX11.addPattern("X11");
        waylandX11.addPattern("XORG");
        waylandX11.addPattern("WAYLAND VS X11");
        waylandX11.addResponse("Wayland is the modern display protocol, X11 (Xorg) is legacy but more compatible.");
        waylandX11.addResponse("Ubuntu uses Wayland by default since 21.04. Switch to X11 at login (gear icon → Ubuntu on Xorg).");
        waylandX11.addResponse("Check current session: 'echo $XDG_SESSION_TYPE' (returns 'wayland' or 'x11')");
        waylandX11.addResponse("Wayland pros: Better security, smoother. Cons: Some apps don't work (screen sharing, some games).");
        waylandX11.addResponse("X11 needed for: Some remote desktop tools, older NVIDIA drivers, certain games.");
        waylandX11.addResponse("Force X11: Edit /etc/gdm3/custom.conf, uncomment 'WaylandEnable=false'");
        waylandX11.addResponse("NVIDIA users: Wayland may cause issues with proprietary drivers - use X11 instead.");
        waylandX11.addResponse("Wayland doesn't support X11 apps natively - uses XWayland compatibility layer.");
        waylandX11.setTopic("desktop");
        rules.add(waylandX11);
    }

    // ===================================================================
    // DISPLAY MANAGERS
    // ===================================================================

    private void initializeDisplayManagerRules() {
        // GDM3
        Rule gdm = new Rule("gdm3", 85);
        gdm.addPattern("GDM");
        gdm.addPattern("GDM3");
        gdm.addPattern("GNOME DISPLAY MANAGER");
        gdm.addResponse("GDM3 is GNOME's display manager (login screen). It's Ubuntu's default.");
        gdm.addResponse("Install GDM3: 'sudo apt install gdm3'");
        gdm.addResponse("GDM3 config: /etc/gdm3/custom.conf");
        gdm.addResponse("Restart GDM3: 'sudo systemctl restart gdm3' (logs you out!)");
        gdm.addResponse("GDM3 auto-login: Edit /etc/gdm3/custom.conf, uncomment AutomaticLoginEnable=true and set AutomaticLogin=username");
        gdm.addResponse("Disable Wayland in GDM3: Edit /etc/gdm3/custom.conf, uncomment WaylandEnable=false");
        gdm.addResponse("GDM3 uses more resources than LightDM but integrates better with GNOME.");
        gdm.addResponse("GDM3 logs: 'journalctl -u gdm3'");
        gdm.setTopic("display-manager");
        rules.add(gdm);

        // LIGHTDM
        Rule lightdm = new Rule("lightdm", 85);
        lightdm.addPattern("LIGHTDM");
        lightdm.addPattern("LIGHT DISPLAY MANAGER");
        lightdm.addResponse("LightDM is a lightweight display manager. Popular with XFCE, MATE.");
        lightdm.addResponse("Install LightDM: 'sudo apt install lightdm'");
        lightdm.addResponse("LightDM config: /etc/lightdm/lightdm.conf");
        lightdm.addResponse("LightDM greeters (login screens): lightdm-gtk-greeter, slick-greeter, webkit2-greeter");
        lightdm.addResponse("Auto-login LightDM: Edit /etc/lightdm/lightdm.conf, set autologin-user=username");
        lightdm.addResponse("Restart LightDM: 'sudo systemctl restart lightdm' (logs you out!)");
        lightdm.addResponse("LightDM is lighter than GDM3 - uses about 50MB less RAM.");
        lightdm.addResponse("Customize LightDM: Install lightdm-gtk-greeter-settings for GUI config tool.");
        lightdm.addResponse("LightDM logs: 'journalctl -u lightdm' or /var/log/lightdm/");
        lightdm.setTopic("display-manager");
        rules.add(lightdm);

        // SDDM
        Rule sddm = new Rule("sddm", 85);
        sddm.addPattern("SDDM");
        sddm.addPattern("SIMPLE DESKTOP DISPLAY MANAGER");
        sddm.addResponse("SDDM is KDE's display manager - clean and Qt-based.");
        sddm.addResponse("Install SDDM: 'sudo apt install sddm'");
        sddm.addResponse("SDDM config: /etc/sddm.conf or /etc/sddm.conf.d/");
        sddm.addResponse("SDDM themes: Install from KDE Store, place in /usr/share/sddm/themes/");
        sddm.addResponse("SDDM auto-login: Create /etc/sddm.conf with [Autologin] User=username Session=plasma.desktop");
        sddm.addResponse("Restart SDDM: 'sudo systemctl restart sddm' (logs you out!)");
        sddm.addResponse("SDDM works great with KDE Plasma but can be used with any desktop.");
        sddm.addResponse("SDDM logs: 'journalctl -u sddm'");
        sddm.setTopic("display-manager");
        rules.add(sddm);

        // DISPLAY MANAGER SWITCHING
        Rule dmSwitch = new Rule("dm-switch", 90);
        dmSwitch.addPattern("SWITCH DISPLAY MANAGER");
        dmSwitch.addPattern("CHANGE DISPLAY MANAGER");
        dmSwitch.addPattern("CHANGE LOGIN SCREEN");
        dmSwitch.addResponse("Switch display manager: 'sudo dpkg-reconfigure [gdm3|lightdm|sddm]'");
        dmSwitch.addResponse("Reconfigure display manager: 'sudo dpkg-reconfigure gdm3' (or lightdm/sddm)");
        dmSwitch.addResponse("Current display manager: 'systemctl status display-manager'");
        dmSwitch.addResponse("Disable display manager (text-only boot): 'sudo systemctl set-default multi-user.target'");
        dmSwitch.addResponse("Enable display manager: 'sudo systemctl set-default graphical.target'");
        dmSwitch.addResponse("Display managers: GDM3 (GNOME), LightDM (universal), SDDM (KDE)");
        dmSwitch.setTopic("display-manager");
        rules.add(dmSwitch);

        // LOGIN SCREEN CUSTOMIZATION
        Rule loginCustom = new Rule("login-custom", 85);
        loginCustom.addPattern("CUSTOMIZE LOGIN");
        loginCustom.addPattern("LOGIN SCREEN");
        loginCustom.addPattern("LOGIN BACKGROUND");
        loginCustom.addResponse("GDM3 background: Copy image to /usr/share/backgrounds/ and use CSS in /etc/alternatives/gdm3.css");
        loginCustom.addResponse("LightDM background: Install lightdm-gtk-greeter-settings: 'sudo apt install lightdm-gtk-greeter-settings'");
        loginCustom.addResponse("SDDM themes: Download from kde-look.org, extract to /usr/share/sddm/themes/");
        loginCustom.addResponse("Set SDDM theme: Edit /etc/sddm.conf, set [Theme] Current=theme-name");
        loginCustom.addResponse("GDM3 logo: /usr/share/pixmaps/debian-logo.png (replace carefully!)");
        loginCustom.addResponse("Login screen music/sounds: Generally not recommended - can annoy users!");
        loginCustom.setTopic("display-manager");
        rules.add(loginCustom);

        // AUTO-LOGIN
        Rule autoLogin = new Rule("auto-login", 85);
        autoLogin.addPattern("AUTO LOGIN");
        autoLogin.addPattern("AUTOMATIC LOGIN");
        autoLogin.addPattern("LOGIN AUTOMATICALLY");
        autoLogin.addResponse("⚠️ Auto-login reduces security! Only use on personal, physically secure computers.");
        autoLogin.addResponse("GDM3 auto-login: Edit /etc/gdm3/custom.conf, set AutomaticLoginEnable=true and AutomaticLogin=yourusername");
        autoLogin.addResponse("LightDM auto-login: Edit /etc/lightdm/lightdm.conf, set autologin-user=yourusername");
        autoLogin.addResponse("SDDM auto-login: Create /etc/sddm.conf with [Autologin] section, User=yourusername");
        autoLogin.addResponse("Disable auto-login: Remove/comment the autologin lines in your display manager config.");
        autoLogin.addResponse("GUI auto-login (GNOME): Settings → Users → Unlock → Enable Automatic Login");
        autoLogin.setTopic("display-manager");
        rules.add(autoLogin);
    }

private void initializeAudioRules() {
    // PULSEAUDIO
    Rule pulseaudio = new Rule("pulseaudio", 85);
    pulseaudio.addPattern("PULSEAUDIO");
    pulseaudio.addPattern("PULSE AUDIO");
    pulseaudio.addResponse("PulseAudio is Ubuntu's traditional audio server. Most apps still use it.");
    pulseaudio.addResponse("Restart PulseAudio: 'pulseaudio -k' (kills it, systemd restarts automatically)");
    pulseaudio.addResponse("PulseAudio GUI: Install pavucontrol: 'sudo apt install pavucontrol'");
    pulseaudio.addResponse("Check PulseAudio status: 'pulseaudio --check' (no output = running)");
    pulseaudio.addResponse("PulseAudio config: ~/.config/pulse/ (user) or /etc/pulse/ (system)");
    pulseaudio.addResponse("List audio devices: 'pactl list sinks' (outputs) and 'pactl list sources' (inputs)");
    pulseaudio.addResponse("Set default device: 'pactl set-default-sink [device-name]'");
    pulseaudio.addResponse("PulseAudio logs: 'journalctl --user -u pulseaudio'");
    pulseaudio.setTopic("audio");
    rules.add(pulseaudio);

    // PIPEWIRE
    Rule pipewire = new Rule("pipewire", 85);
    pipewire.addPattern("PIPEWIRE");
    pipewire.addPattern("PIPE WIRE");
    pipewire.addResponse("PipeWire is the modern audio/video server - Ubuntu 22.10+ uses it by default.");
    pipewire.addResponse("PipeWire replaces both PulseAudio and JACK - better for pro audio!");
    pipewire.addResponse("Check if using PipeWire: 'pactl info | grep \"Server Name\"' (look for PipeWire)");
    pipewire.addResponse("Install PipeWire: 'sudo apt install pipewire pipewire-pulse'");
    pipewire.addResponse("Switch to PipeWire: 'systemctl --user --now enable pipewire pipewire-pulse'");
    pipewire.addResponse("Restart PipeWire: 'systemctl --user restart pipewire pipewire-pulse'");
    pipewire.addResponse("PipeWire still uses PulseAudio tools (pactl, pavucontrol) for compatibility.");
    pipewire.addResponse("PipeWire advantages: Lower latency, better Bluetooth, replaces JACK for pro audio.");
    pipewire.addResponse("PipeWire logs: 'journalctl --user -u pipewire'");
    pipewire.setTopic("audio");
    rules.add(pipewire);

    // ALSA
    Rule alsa = new Rule("alsa", 85);
    alsa.addPattern("ALSA");
    alsa.addPattern("ADVANCED LINUX SOUND");
    alsa.addResponse("ALSA is the low-level Linux audio system. PulseAudio/PipeWire sit on top of it.");
    alsa.addResponse("ALSA mixer: 'alsamixer' - terminal-based volume control");
    alsa.addResponse("List ALSA devices: 'aplay -l' (playback) and 'arecord -l' (recording)");
    alsa.addResponse("Test ALSA: 'speaker-test -c 2' (2-channel stereo test)");
    alsa.addResponse("ALSA config: /etc/asound.conf (system) or ~/.asoundrc (user)");
    alsa.addResponse("Unmute ALSA: Run 'alsamixer', use arrow keys to select, press 'M' to unmute");
    alsa.addResponse("ALSA information: 'cat /proc/asound/cards'");
    alsa.setTopic("audio");
    rules.add(alsa);

    // AUDIO TROUBLESHOOTING
    Rule audioTrouble = new Rule("audio-trouble", 90);
    audioTrouble.addPattern("NO SOUND");
    audioTrouble.addPattern("AUDIO NOT WORKING");
    audioTrouble.addPattern("SOUND NOT WORKING");
    audioTrouble.addPattern("NO AUDIO");
    audioTrouble.addResponse("No sound? 1) Check volume, 2) Check if muted in alsamixer, 3) Restart audio: 'pulseaudio -k'");
    audioTrouble.addResponse("Audio troubleshooting: 'pavucontrol' shows all apps and devices - check output device!");
    audioTrouble.addResponse("Common fix: 'alsamixer' → use arrows to select Master → press 'M' to unmute");
    audioTrouble.addResponse("Wrong audio device? Right-click volume icon → Sound Settings → select correct output.");
    audioTrouble.addResponse("Restart audio stack: 'pulseaudio -k && sudo alsa force-reload'");
    audioTrouble.addResponse("Check audio devices: 'aplay -l' - make sure your device is detected.");
    audioTrouble.addResponse("HDMI audio not working? Select HDMI output in Sound Settings or pavucontrol.");
    audioTrouble.addResponse("Bluetooth audio issues? Check PipeWire - better Bluetooth support than PulseAudio.");
    audioTrouble.setTopic("audio");
    rules.add(audioTrouble);

    // MICROPHONE
    Rule microphone = new Rule("microphone", 85);
    microphone.addPattern("MICROPHONE");
    microphone.addPattern("MIC");
    microphone.addPattern("INPUT DEVICE");
    microphone.addResponse("Test microphone: 'arecord -d 5 test.wav && aplay test.wav' (5-second recording)");
    microphone.addResponse("Microphone settings: pavucontrol → Input Devices tab");
    microphone.addResponse("List input devices: 'pactl list sources' or 'arecord -l'");
    microphone.addResponse("Set default mic: 'pactl set-default-source [device-name]'");
    microphone.addResponse("Mic permissions: Some apps need explicit permission - check Settings → Privacy → Microphone");
    microphone.addResponse("Mic too quiet? Boost in alsamixer or pavucontrol - be careful of feedback!");
    microphone.addResponse("USB mic not detected? Try different USB port, check 'lsusb' to verify detection.");
    microphone.setTopic("audio");
    rules.add(microphone);

    // VOLUME CONTROL
    Rule volume = new Rule("volume", 85);
    volume.addPattern("VOLUME");
    volume.addPattern("VOLUME CONTROL");
    volume.addPattern("ADJUST VOLUME");
    volume.addResponse("GUI volume: pavucontrol - 'sudo apt install pavucontrol'");
    volume.addResponse("Terminal volume: alsamixer (ALSA) or 'pactl set-sink-volume @DEFAULT_SINK@ +5%'");
    volume.addResponse("Per-app volume: pavucontrol → Playback tab → adjust individual apps");
    volume.addResponse("Volume shortcuts: Usually Fn+F keys or media keys on keyboard");
    volume.addResponse("Volume over 100%: pavucontrol allows boosting - can cause distortion!");
    volume.addResponse("Command-line volume: 'amixer set Master 50%' or 'pactl set-sink-volume 0 50%'");
    volume.setTopic("audio");
    rules.add(volume);

    // AUDIO DEVICES
    Rule audioDevices = new Rule("audio-devices", 85);
    audioDevices.addPattern("AUDIO DEVICE");
    audioDevices.addPattern("SOUND DEVICE");
    audioDevices.addPattern("OUTPUT DEVICE");
    audioDevices.addPattern("SWITCH AUDIO");
    audioDevices.addResponse("List output devices: 'pactl list sinks short'");
    audioDevices.addResponse("List input devices: 'pactl list sources short'");
    audioDevices.addResponse("Switch output device: Right-click volume icon → Sound Settings → select device");
    audioDevices.addResponse("Set default output: 'pactl set-default-sink [sink-name]'");
    audioDevices.addResponse("Per-app output device: pavucontrol → Playback tab → dropdown menu for each app");
    audioDevices.addResponse("HDMI audio: Select HDMI device in Sound Settings - may show as monitor name.");
    audioDevices.addResponse("Multiple audio devices? pavucontrol is your best friend!");
    audioDevices.setTopic("audio");
    rules.add(audioDevices);
}
private void initializeGraphicsDriverRules() {
    // NVIDIA
    Rule nvidia = new Rule("nvidia", 90);
    nvidia.addPattern("NVIDIA");
    nvidia.addPattern("NVIDIA DRIVER");
    nvidia.addPattern("GEFORCE");
    nvidia.addResponse("⚠️ NVIDIA on Linux requires proprietary drivers for best performance.");
    nvidia.addResponse("Install NVIDIA driver: 'sudo ubuntu-drivers autoinstall' (automatic) or use Software & Updates → Additional Drivers");
    nvidia.addResponse("Check NVIDIA driver: 'nvidia-smi' (shows GPU, driver version, usage)");
    nvidia.addResponse("Recommended driver: 'ubuntu-drivers devices' (shows recommended version)");
    nvidia.addResponse("Install specific version: 'sudo apt install nvidia-driver-535' (replace 535 with version)");
    nvidia.addResponse("NVIDIA proprietary vs nouveau: Proprietary = better performance/features, nouveau = open-source/limited");
    nvidia.addResponse("Wayland + NVIDIA = problems! Use X11 session for better stability.");
    nvidia.addResponse("NVIDIA settings: 'nvidia-settings' (GUI) - adjust fan, overclock, multi-monitor");
    nvidia.addResponse("NVIDIA drivers sometimes break on kernel updates - keep 'nouveau' as backup!");
    nvidia.addResponse("Remove NVIDIA driver: 'sudo apt purge nvidia-*' (boots with nouveau)");
    nvidia.setTopic("graphics");
    rules.add(nvidia);

    // NVIDIA NOUVEAU
    Rule nouveau = new Rule("nouveau", 85);
    nouveau.addPattern("NOUVEAU");
    nouveau.addPattern("OPEN SOURCE NVIDIA");
    nouveau.addResponse("Nouveau is the open-source NVIDIA driver - works out of box but limited performance.");
    nouveau.addResponse("Nouveau vs proprietary: Nouveau = stable but slow, Proprietary = fast but can break.");
    nouveau.addResponse("Switch to nouveau: Remove proprietary driver, reboot. Nouveau loads automatically.");
    nouveau.addResponse("Nouveau good for: Basic desktop, older cards, troubleshooting proprietary issues.");
    nouveau.addResponse("Nouveau bad for: Gaming, CUDA, video rendering, multiple monitors.");
    nouveau.addResponse("Check if using nouveau: 'lsmod | grep nouveau'");
    nouveau.setTopic("graphics");
    rules.add(nouveau);

    // AMD
    Rule amd = new Rule("amd", 85);
    amd.addPattern("AMD");
    amd.addPattern("RADEON");
    amd.addPattern("AMDGPU");
    amd.addResponse("AMD graphics work great on Linux! AMDGPU driver is built into the kernel.");
    amd.addResponse("AMD drivers: AMDGPU (modern, default) or Radeon (older cards)");
    amd.addResponse("Check AMD driver: 'lspci -k | grep -A 3 VGA' (look for 'amdgpu' or 'radeon')");
    amd.addResponse("AMD doesn't need extra driver installation - works out of box!");
    amd.addResponse("AMD performance: Mesa drivers improve constantly - update for better gaming!");
    amd.addResponse("Install latest Mesa: 'sudo add-apt-repository ppa:kisak/kisak-mesa && sudo apt update && sudo apt upgrade'");
    amd.addResponse("AMD GPU info: 'sudo apt install radeontop' then run 'radeontop'");
    amd.addResponse("AMD Vulkan: Install 'sudo apt install mesa-vulkan-drivers' (usually pre-installed)");
    amd.addResponse("AMD works better on Wayland than NVIDIA!");
    amd.setTopic("graphics");
    rules.add(amd);

    // INTEL GRAPHICS
    Rule intel = new Rule("intel-gpu", 85);
    intel.addPattern("INTEL GRAPHICS");
    intel.addPattern("INTEL GPU");
    intel.addPattern("INTEGRATED GRAPHICS");
    intel.addResponse("Intel graphics work perfectly on Linux - drivers built into kernel.");
    intel.addResponse("Intel driver: i915 (older) or xe (newer, Arc GPUs)");
    intel.addResponse("Check Intel driver: 'lspci -k | grep -A 3 VGA'");
    intel.addResponse("Intel graphics need no manual installation - just works!");
    intel.addResponse("Intel GPU info: 'sudo apt install intel-gpu-tools' then 'intel_gpu_top'");
    intel.addResponse("Update Intel firmware: 'sudo apt install intel-microcode' (usually pre-installed)");
    intel.addResponse("Intel Vulkan: 'sudo apt install mesa-vulkan-drivers intel-media-va-driver'");
    intel.addResponse("Intel works great with Wayland!");
    intel.setTopic("graphics");
    rules.add(intel);

    // DRIVER INSTALLATION
    Rule driverInstall = new Rule("driver-install", 90);
    driverInstall.addPattern("INSTALL DRIVER");
    driverInstall.addPattern("GRAPHICS DRIVER");
    driverInstall.addPattern("GPU DRIVER");
    driverInstall.addResponse("Easy driver install: Software & Updates → Additional Drivers → Select recommended → Apply");
    driverInstall.addResponse("Command-line: 'sudo ubuntu-drivers autoinstall' (installs all recommended drivers)");
    driverInstall.addResponse("Check available drivers: 'ubuntu-drivers devices'");
    driverInstall.addResponse("AMD/Intel: Drivers built-in, no installation needed!");
    driverInstall.addResponse("NVIDIA: Requires proprietary driver for good performance.");
    driverInstall.addResponse("After driver install: Reboot required!");
    driverInstall.setTopic("graphics");
    rules.add(driverInstall);

    // HYBRID GRAPHICS
    Rule hybrid = new Rule("hybrid-graphics", 90);
    hybrid.addPattern("HYBRID GRAPHICS");
    hybrid.addPattern("OPTIMUS");
    hybrid.addPattern("SWITCHABLE GRAPHICS");
    hybrid.addPattern("DUAL GPU");
    hybrid.addResponse("Hybrid graphics (Intel + NVIDIA): Use 'prime-select' to switch GPUs.");
    hybrid.addResponse("Switch to NVIDIA: 'sudo prime-select nvidia' then reboot.");
    hybrid.addResponse("Switch to Intel: 'sudo prime-select intel' then reboot (saves battery!)");
    hybrid.addResponse("Check current GPU: 'prime-select query'");
    hybrid.addResponse("On-demand mode: 'sudo prime-select on-demand' (uses Intel, NVIDIA when needed)");
    hybrid.addResponse("Run app on NVIDIA: 'prime-run glxgears' or '__NV_PRIME_RENDER_OFFLOAD=1 [app]'");
    hybrid.addResponse("Hybrid graphics drain battery - use Intel for normal tasks, NVIDIA for gaming/rendering.");
    hybrid.addResponse("Optimus laptops: Ubuntu 20.04+ has good support with prime-select.");
    hybrid.setTopic("graphics");
    rules.add(hybrid);
}
private void initializeSystemdRules() {
    // SYSTEMCTL COMMANDS
    Rule systemctl = new Rule("systemctl", 90);
    systemctl.addPattern("SYSTEMCTL");
    systemctl.addPattern("SYSTEMD");
    systemctl.addPattern("SERVICE");
    systemctl.addResponse("systemctl is the systemd service manager. Controls all services/daemons.");
    systemctl.addResponse("List all services: 'systemctl list-units --type=service'");
    systemctl.addResponse("Service status: 'systemctl status [service-name]'");
    systemctl.addResponse("Start service: 'sudo systemctl start [service]'");
    systemctl.addResponse("Stop service: 'sudo systemctl stop [service]'");
    systemctl.addResponse("Restart service: 'sudo systemctl restart [service]'");
    systemctl.addResponse("Reload config: 'sudo systemctl reload [service]' (doesn't restart)");
    systemctl.addResponse("systemctl doesn't need .service extension: 'systemctl status ssh' = 'systemctl status ssh.service'");
    systemctl.setTopic("systemd");
    rules.add(systemctl);

    // ENABLE/DISABLE SERVICES
    Rule enableService = new Rule("enable-service", 90);
    enableService.addPattern("ENABLE SERVICE");
    enableService.addPattern("DISABLE SERVICE");
    enableService.addPattern("START AT BOOT");
    enableService.addPattern("AUTOSTART SERVICE");
    enableService.addResponse("Enable service (start at boot): 'sudo systemctl enable [service]'");
    enableService.addResponse("Disable service (don't start at boot): 'sudo systemctl disable [service]'");
    enableService.addResponse("Enable and start now: 'sudo systemctl enable --now [service]'");
    enableService.addResponse("Disable and stop now: 'sudo systemctl disable --now [service]'");
    enableService.addResponse("Check if enabled: 'systemctl is-enabled [service]'");
    enableService.addResponse("List enabled services: 'systemctl list-unit-files --state=enabled'");
    enableService.addResponse("Enable ≠ Start: 'enable' = start at boot, 'start' = start now");
    enableService.setTopic("systemd");
    rules.add(enableService);

    // SERVICE STATUS
    Rule serviceStatus = new Rule("service-status", 85);
    serviceStatus.addPattern("SERVICE STATUS");
    serviceStatus.addPattern("CHECK SERVICE");
    serviceStatus.addPattern("IS SERVICE RUNNING");
    serviceStatus.addResponse("Check service: 'systemctl status [service-name]'");
    serviceStatus.addResponse("Is service running? 'systemctl is-active [service]'");
    serviceStatus.addResponse("Is service enabled? 'systemctl is-enabled [service]'");
    serviceStatus.addResponse("Service failed? 'systemctl status [service]' shows error, 'journalctl -u [service]' shows logs");
    serviceStatus.addResponse("List failed services: 'systemctl --failed'");
    serviceStatus.addResponse("All services: 'systemctl list-units --type=service --all'");
    serviceStatus.setTopic("systemd");
    rules.add(serviceStatus);

    // JOURNALCTL LOGS
    Rule journalctl = new Rule("journalctl", 90);
    journalctl.addPattern("JOURNALCTL");
    journalctl.addPattern("SYSTEMD LOGS");
    journalctl.addPattern("SERVICE LOGS");
    journalctl.addResponse("View all logs: 'journalctl' (press 'q' to quit)");
    journalctl.addResponse("Service logs: 'journalctl -u [service-name]'");
    journalctl.addResponse("Follow logs (live): 'journalctl -fu [service]'");
    journalctl.addResponse("Boot logs: 'journalctl -b' (current boot) or 'journalctl -b -1' (previous boot)");
    journalctl.addResponse("Recent logs: 'journalctl -xe' (last few entries with explanations)");
    journalctl.addResponse("Time range: 'journalctl --since \"1 hour ago\"' or '--since \"2023-01-01\"'");
    journalctl.addResponse("Kernel logs: 'journalctl -k' (same as dmesg but persistent)");
    journalctl.addResponse("Clear old logs: 'sudo journalctl --vacuum-time=2weeks' (keep 2 weeks)");
    journalctl.addResponse("journalctl size: 'journalctl --disk-usage'");
    journalctl.setTopic("systemd");
    rules.add(journalctl);

    // CREATING CUSTOM SERVICES
    Rule customService = new Rule("custom-service", 85);
    customService.addPattern("CREATE SERVICE");
    customService.addPattern("CUSTOM SERVICE");
    customService.addPattern("SYSTEMD UNIT");
    customService.addResponse("Create service: Put .service file in /etc/systemd/system/");
    customService.addResponse("Service file format:\n[Unit]\nDescription=My Service\n[Service]\nExecStart=/path/to/script\n[Install]\nWantedBy=multi-user.target");
    customService.addResponse("After creating service: 'sudo systemctl daemon-reload' to reload systemd");
    customService.addResponse("Test custom service: 'sudo systemctl start my-service && systemctl status my-service'");
    customService.addResponse("Service types: Type=simple (default), Type=forking (daemons), Type=oneshot (runs once)");
    customService.addResponse("Restart policies: Restart=always, Restart=on-failure, Restart=no");
    customService.addResponse("User services: Put in ~/.config/systemd/user/, use 'systemctl --user' commands");
    customService.addResponse("Service dependencies: After=network.target, Requires=postgresql.service, etc.");
    customService.setTopic("systemd");
    rules.add(customService);
}
private void initializeBootGrubRules() {
    // GRUB CONFIGURATION
    Rule grubConfig = new Rule("grub-config", 90);
    grubConfig.addPattern("GRUB");
    grubConfig.addPattern("GRUB CONFIG");
    grubConfig.addPattern("BOOTLOADER");
    grubConfig.addResponse("GRUB config: /etc/default/grub (edit this, not grub.cfg!)");
    grubConfig.addResponse("After editing GRUB: 'sudo update-grub' to apply changes");
    grubConfig.addResponse("GRUB timeout: Edit GRUB_TIMEOUT in /etc/default/grub (seconds)");
    grubConfig.addResponse("Hide GRUB menu: Set GRUB_TIMEOUT=0 and GRUB_HIDDEN_TIMEOUT=0");
    grubConfig.addResponse("Show GRUB menu: Comment out GRUB_HIDDEN_TIMEOUT, set GRUB_TIMEOUT=10");
    grubConfig.addResponse("GRUB theme: GRUB_GFXMODE=1920x1080, GRUB_THEME=/path/to/theme");
    grubConfig.addResponse("GRUB commands: Hold Shift at boot to access GRUB menu");
    grubConfig.addResponse("⚠️ Never edit /boot/grub/grub.cfg directly - it's auto-generated!");
    grubConfig.setTopic("boot");
    rules.add(grubConfig);

    // KERNEL PARAMETERS
    Rule kernelParams = new Rule("kernel-params", 90);
    kernelParams.addPattern("KERNEL PARAMETER");
    kernelParams.addPattern("BOOT PARAMETER");
    kernelParams.addPattern("GRUB CMDLINE");
    kernelParams.addResponse("Add kernel parameters: Edit GRUB_CMDLINE_LINUX in /etc/default/grub");
    kernelParams.addResponse("Temporary kernel param: Press 'e' in GRUB menu, edit linux line, press F10");
    kernelParams.addResponse("Common parameters: quiet (hide messages), splash (show logo), nomodeset (basic graphics)");
    kernelParams.addResponse("nomodeset: Fixes some graphics issues - boots with basic VGA driver");
    kernelParams.addResponse("After editing kernel params: 'sudo update-grub' then reboot");
    kernelParams.addResponse("View current params: 'cat /proc/cmdline'");
    kernelParams.addResponse("Disable quiet/splash: Remove from GRUB_CMDLINE_LINUX to see boot messages");
    kernelParams.addResponse("Advanced params: acpi=off, noapic, intel_iommu=on, etc. (hardware-specific)");
    kernelParams.setTopic("boot");
    rules.add(kernelParams);

    // BOOT ORDER
    Rule bootOrder = new Rule("boot-order", 85);
    bootOrder.addPattern("BOOT ORDER");
    bootOrder.addPattern("DEFAULT OS");
    bootOrder.addPattern("GRUB DEFAULT");
    bootOrder.addResponse("Change default OS: Edit GRUB_DEFAULT in /etc/default/grub");
    bootOrder.addResponse("GRUB_DEFAULT=0 boots first entry, GRUB_DEFAULT=2 boots third entry");
    bootOrder.addResponse("Boot saved entry: GRUB_DEFAULT=saved and GRUB_SAVEDEFAULT=true (remembers last choice)");
    bootOrder.addResponse("After changing default: 'sudo update-grub'");
    bootOrder.addResponse("List boot entries: 'grep menuentry /boot/grub/grub.cfg'");
    bootOrder.addResponse("Boot specific kernel: Count menuentry positions (0-indexed) in grub.cfg");
    bootOrder.setTopic("boot");
    rules.add(bootOrder);

    // DUAL BOOT
    Rule dualBoot = new Rule("dual-boot", 90);
    dualBoot.addPattern("DUAL BOOT");
    dualBoot.addPattern("WINDOWS BOOT");
    dualBoot.addPattern("GRUB WINDOWS");
    dualBoot.addResponse("Dual boot Ubuntu/Windows: GRUB detects Windows automatically with 'sudo update-grub'");
    dualBoot.addResponse("Windows not showing? 'sudo apt install os-prober && sudo update-grub'");
    dualBoot.addResponse("⚠️ Install Windows FIRST, then Ubuntu - easier GRUB setup!");
    dualBoot.addResponse("Boot Windows default: Change GRUB_DEFAULT to Windows entry number");
    dualBoot.addResponse("Windows time issue: Dual boot causes time problems - fix: 'timedatectl set-local-rtc 1'");
    dualBoot.addResponse("UEFI dual boot: Both OSes must use same boot mode (both UEFI or both Legacy)");
    dualBoot.addResponse("Fix broken dual boot: Boot Ubuntu live USB, mount partition, reinstall GRUB");
    dualBoot.addResponse("os-prober disabled in Ubuntu 22.04+: Enable in /etc/default/grub: GRUB_DISABLE_OS_PROBER=false");
    dualBoot.setTopic("boot");
    rules.add(dualBoot);

    // RECOVERY MODE
    Rule recovery = new Rule("recovery", 90);
    recovery.addPattern("RECOVERY MODE");
    recovery.addPattern("RECOVERY BOOT");
    recovery.addPattern("SINGLE USER MODE");
    recovery.addResponse("Access recovery mode: Select 'Advanced options' in GRUB menu → Recovery mode");
    recovery.addResponse("Can't see GRUB? Hold Shift during boot to show menu");
    recovery.addResponse("Recovery mode boots to root shell without GUI - good for fixing issues");
    recovery.addResponse("Recovery menu options: fsck (disk check), root (root shell), network (enable network)");
    recovery.addResponse("Filesystem read-only in recovery? Select 'remount' option or 'mount -o remount,rw /'");
    recovery.addResponse("Recovery mode use cases: Reset password, fix broken packages, repair filesystem");
    recovery.addResponse("Emergency mode: More minimal than recovery - 'systemctl emergency'");
    recovery.setTopic("boot");
    rules.add(recovery);
}
private void initializeDisplayRules() {
    // SCREEN RESOLUTION
    Rule resolution = new Rule("resolution", 90);
    resolution.addPattern("RESOLUTION");
    resolution.addPattern("SCREEN RESOLUTION");
    resolution.addPattern("DISPLAY RESOLUTION");
    resolution.addPattern("CHANGE RESOLUTION");
    resolution.addResponse("Change resolution: Settings → Displays → Select resolution → Apply");
    resolution.addResponse("Command-line: 'xrandr' to list, 'xrandr --output [display] --mode 1920x1080' to set");
    resolution.addResponse("List available resolutions: 'xrandr' (look for connected displays)");
    resolution.addResponse("Custom resolution: 'cvt 1920 1080' (generates modeline), then 'xrandr --newmode' and '--addmode'");
    resolution.addResponse("Resolution not listed? May need to add custom mode with xrandr or edit X11 config");
    resolution.addResponse("Wayland resolution: Use Settings GUI - xrandr doesn't work on Wayland!");
    resolution.addResponse("Reset resolution: Log out and back in, or 'xrandr --auto'");
    resolution.setTopic("display");
    rules.add(resolution);

    // MULTIPLE MONITORS
    Rule multiMonitor = new Rule("multi-monitor", 90);
    multiMonitor.addPattern("MULTIPLE MONITOR");
    multiMonitor.addPattern("DUAL MONITOR");
    multiMonitor.addPattern("TWO MONITOR");
    multiMonitor.addPattern("MULTI DISPLAY");
    multiMonitor.addResponse("Multiple monitors: Settings → Displays → Arrange displays, set primary");
    multiMonitor.addResponse("xrandr multi-monitor: 'xrandr --output HDMI-1 --right-of eDP-1 --auto'");
    multiMonitor.addResponse("List connected displays: 'xrandr | grep connected'");
    multiMonitor.addResponse("Set primary display: Settings → Displays → Select monitor → Toggle 'Primary Display'");
    multiMonitor.addResponse("Mirror displays: Settings → Displays → Select 'Mirror' or 'Same as'");
    multiMonitor.addResponse("Extended desktop: Settings → Displays → Arrange monitors side-by-side");
    multiMonitor.addResponse("Position monitors: Drag display icons in Settings → Displays to arrange");
    multiMonitor.addResponse("Per-monitor scaling: GNOME/Wayland supports different scaling per display!");
    multiMonitor.addResponse("Save multi-monitor setup: GNOME remembers configs, or use autorandr: 'sudo apt install autorandr'");
    multiMonitor.setTopic("display");
    rules.add(multiMonitor);

    // REFRESH RATE
    Rule refreshRate = new Rule("refresh-rate", 85);
    refreshRate.addPattern("REFRESH RATE");
    refreshRate.addPattern("HZ");
    refreshRate.addPattern("144HZ");
    refreshRate.addPattern("60HZ");
    refreshRate.addResponse("Change refresh rate: Settings → Displays → Select refresh rate from dropdown");
    refreshRate.addResponse("xrandr refresh rate: 'xrandr --output HDMI-1 --mode 1920x1080 --rate 144'");
    refreshRate.addResponse("Check current refresh rate: 'xrandr | grep \\*' (asterisk shows current mode)");
    refreshRate.addResponse("144Hz not listed? May need custom modeline or driver update");
    refreshRate.addResponse("Wayland refresh rate: Settings GUI only - xrandr doesn't work!");
    refreshRate.addResponse("Gaming monitors: Make sure you're using DisplayPort or HDMI 2.0+ for high refresh rates");
    refreshRate.setTopic("display");
    rules.add(refreshRate);

    // DISPLAY SCALING
    Rule scaling = new Rule("scaling", 85);
    scaling.addPattern("SCALING");
    scaling.addPattern("DISPLAY SCALING");
    scaling.addPattern("FRACTIONAL SCALING");
    scaling.addPattern("HIDPI");
    scaling.addResponse("Display scaling: Settings → Displays → Scale (100%, 125%, 150%, 200%)");
    scaling.addResponse("Fractional scaling: Enable in GNOME Tweaks or 'gsettings set org.gnome.mutter experimental-features \"['scale-monitor-framebuffer']\"'");
    scaling.addResponse("HiDPI/4K displays: Use 200% scaling for comfortable viewing");
    scaling.addResponse("Per-monitor scaling: Wayland supports different scaling per display!");
    scaling.addResponse("X11 scaling: Less flexible than Wayland - may result in blurry text");
    scaling.addResponse("App-specific scaling: Some apps don't respect system scaling - check app settings");
    scaling.addResponse("Text scaling only: Settings → Accessibility → Large Text");
    scaling.setTopic("display");
    rules.add(scaling);

    // SCREEN ROTATION
    Rule rotation = new Rule("rotation", 80);
    rotation.addPattern("ROTATE");
    rotation.addPattern("ROTATION");
    rotation.addPattern("SCREEN ROTATION");
    rotation.addPattern("PORTRAIT");
    rotation.addResponse("Rotate display: Settings → Displays → Orientation (Landscape/Portrait/etc)");
    rotation.addResponse("xrandr rotation: 'xrandr --output HDMI-1 --rotate left' (left/right/inverted/normal)");
    rotation.addResponse("Tablet rotation: Some tablets auto-rotate - install iio-sensor-proxy");
    rotation.addResponse("Lock rotation: GNOME: Settings → Displays → Lock rotation toggle");
    rotation.setTopic("display");
    rules.add(rotation);

    // XRANDR
    Rule xrandr = new Rule("xrandr", 85);
    xrandr.addPattern("XRANDR");
    xrandr.addResponse("xrandr: X11 display configuration tool (doesn't work on Wayland!)");
    xrandr.addResponse("List displays: 'xrandr' (shows all outputs and modes)");
    xrandr.addResponse("Set resolution: 'xrandr --output [DISPLAY] --mode 1920x1080'");
    xrandr.addResponse("Turn off display: 'xrandr --output HDMI-1 --off'");
    xrandr.addResponse("Brightness: 'xrandr --output eDP-1 --brightness 0.8' (0.0-1.0)");
    xrandr.addResponse("xrandr doesn't work? You're probably on Wayland - use Settings GUI instead.");
    xrandr.setTopic("display");
    rules.add(xrandr);
}
private void initializePrinterScannerRules() {
    // CUPS
    Rule cups = new Rule("cups", 85);
    cups.addPattern("CUPS");
    cups.addPattern("PRINTER");
    cups.addPattern("PRINT");
    cups.addResponse("CUPS is Ubuntu's printing system. Web interface: http://localhost:631");
    cups.addResponse("Install CUPS: 'sudo apt install cups' (usually pre-installed)");
    cups.addResponse("Add printer: Settings → Printers → Add Printer or http://localhost:631");
    cups.addResponse("CUPS logs: /var/log/cups/ or 'journalctl -u cups'");
    cups.addResponse("Restart CUPS: 'sudo systemctl restart cups'");
    cups.addResponse("CUPS config: /etc/cups/cupsd.conf");
    cups.setTopic("printing");
    rules.add(cups);

    // PRINTER INSTALLATION
    Rule printerInstall = new Rule("printer-install", 90);
    printerInstall.addPattern("INSTALL PRINTER");
    printerInstall.addPattern("ADD PRINTER");
    printerInstall.addPattern("SETUP PRINTER");
    printerInstall.addResponse("Add printer: Settings → Printers → Add (Ubuntu auto-detects most printers!)");
    printerInstall.addResponse("Web interface: http://localhost:631 → Administration → Add Printer");
    printerInstall.addResponse("USB printer: Plug in, Ubuntu detects automatically");
    printerInstall.addResponse("Network printer: Settings → Printers → Add → Select network printer");
    printerInstall.addResponse("HP printers: Install 'sudo apt install hplip' then 'hp-setup'");
    printerInstall.addResponse("Brother printers: Download drivers from Brother website (usually .deb files)");
    printerInstall.addResponse("Generic printer: Ubuntu includes many drivers - try 'Generic PCL 6' or 'Generic PostScript'");
    printerInstall.addResponse("Printer not found? Check network connection, firewall, printer IP address");
    printerInstall.setTopic("printing");
    rules.add(printerInstall);

    // PRINTER TROUBLESHOOTING
    Rule printerTrouble = new Rule("printer-trouble", 90);
    printerTrouble.addPattern("PRINTER NOT WORKING");
    printerTrouble.addPattern("PRINTER ERROR");
    printerTrouble.addPattern("CAN'T PRINT");
    printerTrouble.addResponse("Printer not printing? 1) Check power/cables, 2) Check printer queue, 3) Restart CUPS");
    printerTrouble.addResponse("Clear print queue: http://localhost:631 → Printers → Select printer → Maintenance → Cancel All Jobs");
    printerTrouble.addResponse("Restart CUPS: 'sudo systemctl restart cups'");
    printerTrouble.addResponse("Check printer status: 'lpstat -p' (shows all printers and status)");
    printerTrouble.addResponse("Test print: 'echo \"Test\" | lp' or 'lp /usr/share/cups/data/testprint'");
    printerTrouble.addResponse("Driver issues? Try different driver or download from manufacturer");
    printerTrouble.addResponse("Network printer can't connect? Check firewall: 'sudo ufw allow ipp'");
    printerTrouble.addResponse("CUPS error logs: http://localhost:631 → Administration → View Error Log");
    printerTrouble.setTopic("printing");
    rules.add(printerTrouble);

    // SCANNER
    Rule scanner = new Rule("scanner", 85);
    scanner.addPattern("SCANNER");
    scanner.addPattern("SCAN");
    scanner.addPattern("SANE");
    scanner.addResponse("Scanning uses SANE backend. Install Simple Scan: 'sudo apt install simple-scan'");
    scanner.addResponse("Simple Scan: Easy GUI scanner app - usually pre-installed");
    scanner.addResponse("List scanners: 'scanimage -L'");
    scanner.addResponse("Command-line scan: 'scanimage --format=png > output.png'");
    scanner.addResponse("HP scanner: Use 'hp-scan' from hplip package");
    scanner.addResponse("Scanner not detected? Install drivers: 'sudo apt install sane sane-utils'");
    scanner.addResponse("Network scanner: May need to add IP to /etc/sane.d/net.conf");
    scanner.addResponse("Test scanner: Run 'scanadf --test' or 'scanimage --test'");
    scanner.setTopic("printing");
    rules.add(scanner);
}
private void initializeBluetoothRules() {
    // BLUETOOTH SETUP
    Rule bluetooth = new Rule("bluetooth", 90);
    bluetooth.addPattern("BLUETOOTH");
    bluetooth.addPattern("BT");
    bluetooth.addResponse("Bluetooth GUI: Settings → Bluetooth → Toggle on → Pair devices");
    bluetooth.addResponse("Install Bluetooth: 'sudo apt install bluez' (usually pre-installed)");
    bluetooth.addResponse("Start Bluetooth: 'sudo systemctl start bluetooth'");
    bluetooth.addResponse("Enable at boot: 'sudo systemctl enable bluetooth'");
    bluetooth.addResponse("Command-line Bluetooth: 'bluetoothctl'");
    bluetooth.addResponse("Check Bluetooth status: 'systemctl status bluetooth'");
    bluetooth.addResponse("Bluetooth not working? Check if hardware switch is on (some laptops)");
    bluetooth.setTopic("bluetooth");
    rules.add(bluetooth);

    // PAIRING DEVICES
    Rule btPair = new Rule("bt-pair", 90);
    btPair.addPattern("PAIR BLUETOOTH");
    btPair.addPattern("CONNECT BLUETOOTH");
    btPair.addPattern("BLUETOOTH DEVICE");
    btPair.addResponse("Pair device: Settings → Bluetooth → Your device appears → Click → Pair");
    btPair.addResponse("Command-line pairing: 'bluetoothctl' → 'scan on' → 'pair [MAC]' → 'connect [MAC]'");
    btPair.addResponse("Trust device (auto-connect): In bluetoothctl: 'trust [MAC]'");
    btPair.addResponse("Remove device: Settings → Bluetooth → Device → Remove");
    btPair.addResponse("Pairing code: Some devices show code on screen - must match on both devices");
    btPair.addResponse("Device not showing? Make sure it's in pairing mode (usually hold button)");
    btPair.setTopic("bluetooth");
    rules.add(btPair);

    // BLUETOOTH TROUBLESHOOTING
    Rule btTrouble = new Rule("bt-trouble", 90);
    btTrouble.addPattern("BLUETOOTH NOT WORKING");
    btTrouble.addPattern("BLUETOOTH ERROR");
    btTrouble.addPattern("BT NOT WORKING");
    btTrouble.addResponse("Bluetooth not working? 1) Check if enabled in BIOS, 2) Restart bluetooth service");
    btTrouble.addResponse("Restart Bluetooth: 'sudo systemctl restart bluetooth'");
    btTrouble.addResponse("Bluetooth blocked? Check 'rfkill list' - unblock: 'rfkill unblock bluetooth'");
    btTrouble.addResponse("Device won't pair? Remove device, restart Bluetooth, try again");
    btTrouble.addResponse("Audio quality issues? PipeWire has better Bluetooth codec support than PulseAudio");
    btTrouble.addResponse("Bluetooth keeps disconnecting? Disable power management: Edit /etc/bluetooth/main.conf → [Policy] AutoEnable=true");
    btTrouble.addResponse("Check Bluetooth adapter: 'hciconfig' or 'bluetoothctl show'");
    btTrouble.addResponse("No Bluetooth adapter? 'lsusb | grep -i bluetooth' to check if detected");
    btTrouble.addResponse("Bluetooth logs: 'journalctl -u bluetooth'");
    btTrouble.setTopic("bluetooth");
    rules.add(btTrouble);

    // BLUETOOTH AUDIO
    Rule btAudio = new Rule("bt-audio", 85);
    btAudio.addPattern("BLUETOOTH AUDIO");
    btAudio.addPattern("BLUETOOTH HEADPHONES");
    btAudio.addPattern("BLUETOOTH SPEAKER");
    btAudio.addResponse("Bluetooth audio: Pair device → Select in Sound Settings → Output Device");
    btAudio.addResponse("Better Bluetooth audio: Use PipeWire instead of PulseAudio (Ubuntu 22.10+)");
    btAudio.addResponse("Bluetooth codec: PipeWire supports AAC, aptX, LDAC for better quality");
    btAudio.addResponse("Audio stuttering? Disable WiFi power management or switch to 5GHz WiFi");
    btAudio.addResponse("Microphone on Bluetooth headset: May need to switch profile in pavucontrol");
    btAudio.addResponse("Bluetooth headset profiles: A2DP (stereo music) vs HSP/HFP (mono with mic)");
    btAudio.setTopic("bluetooth");
    rules.add(btAudio);
}
private void initializeUserPermissionRules() {
    // USER MANAGEMENT
    Rule userMgmt = new Rule("user-mgmt", 90);
    userMgmt.addPattern("USER");
    userMgmt.addPattern("ADD USER");
    userMgmt.addPattern("CREATE USER");
    userMgmt.addPattern("DELETE USER");
    userMgmt.addResponse("Add user: 'sudo adduser username' (interactive) or 'sudo useradd username' (manual)");
    userMgmt.addResponse("Delete user: 'sudo deluser username' (keep home) or 'sudo deluser --remove-home username'");
    userMgmt.addResponse("List users: 'cat /etc/passwd' or 'cut -d: -f1 /etc/passwd'");
    userMgmt.addResponse("Current user: 'whoami' or 'echo $USER'");
    userMgmt.addResponse("Change password: 'passwd' (own) or 'sudo passwd username' (other user)");
    userMgmt.addResponse("Lock user account: 'sudo passwd -l username', Unlock: 'sudo passwd -u username'");
    userMgmt.addResponse("User info: 'id username' (shows UID, GID, groups)");
    userMgmt.addResponse("Switch user: 'su - username' or 'sudo -i -u username'");
    userMgmt.addResponse("GUI user management: Settings → Users (needs admin privileges)");
    userMgmt.setTopic("users");
    rules.add(userMgmt);

    // GROUPS
    Rule groups = new Rule("groups", 90);
    groups.addPattern("GROUP");
    groups.addPattern("GROUPS");
    groups.addPattern("ADD TO GROUP");
    groups.addPattern("USER GROUP");
    groups.addResponse("Add user to group: 'sudo usermod -aG groupname username'");
    groups.addResponse("List user's groups: 'groups username' or just 'groups' for yourself");
    groups.addResponse("List all groups: 'cat /etc/group' or 'cut -d: -f1 /etc/group'");
    groups.addResponse("Create group: 'sudo groupadd groupname'");
    groups.addResponse("Delete group: 'sudo groupdel groupname'");
    groups.addResponse("Remove user from group: 'sudo gpasswd -d username groupname'");
    groups.addResponse("Common groups: sudo (admin), docker (Docker access), www-data (web server)");
    groups.addResponse("Group changes need logout/login or 'newgrp groupname' to take effect!");
    groups.addResponse("Primary vs supplementary groups: Primary group is default for new files");
    groups.setTopic("users");
    rules.add(groups);

    // FILE PERMISSIONS
    Rule permissions = new Rule("permissions", 90);
    permissions.addPattern("PERMISSION");
    permissions.addPattern("FILE PERMISSION");
    permissions.addPattern("CHMOD");
    permissions.addPattern("CHOWN");
    permissions.addResponse("View permissions: 'ls -l' (shows rwxrwxrwx format)");
    permissions.addResponse("Change permissions: 'chmod 755 file' (rwxr-xr-x) or 'chmod +x file' (add executable)");
    permissions.addResponse("Permission format: rwx = read(4) write(2) execute(1), three groups: owner, group, others");
    permissions.addResponse("Common permissions: 644 (rw-r--r--), 755 (rwxr-xr-x), 777 (rwxrwxrwx - AVOID!)");
    permissions.addResponse("Change owner: 'sudo chown user:group file'");
    permissions.addResponse("Recursive permissions: 'chmod -R 755 folder/' (applies to all files/folders inside)");
    permissions.addResponse("Make script executable: 'chmod +x script.sh'");
    permissions.addResponse("Permission denied? Need sudo or change ownership/permissions");
    permissions.addResponse("Directory permissions: Need 'x' to enter directory, 'r' to list contents");
    permissions.setTopic("users");
    rules.add(permissions);

    // SUDO CONFIGURATION
    Rule sudoConfig = new Rule("sudo-config", 90);
    sudoConfig.addPattern("SUDO");
    sudoConfig.addPattern("SUDOERS");
    sudoConfig.addPattern("SUDO ACCESS");
    sudoConfig.addPattern("ADMIN RIGHTS");
    sudoConfig.addResponse("Add sudo access: 'sudo usermod -aG sudo username' (logout required!)");
    sudoConfig.addResponse("Edit sudoers: 'sudo visudo' (NEVER edit /etc/sudoers directly!)");
    sudoConfig.addResponse("Check sudo access: 'sudo -l' (lists what you can run with sudo)");
    sudoConfig.addResponse("Passwordless sudo: Add 'username ALL=(ALL) NOPASSWD:ALL' in visudo (⚠️ INSECURE!)");
    sudoConfig.addResponse("Sudo timeout: Sudo remembers password for 15 minutes by default");
    sudoConfig.addResponse("Reset sudo password timeout: 'sudo -k'");
    sudoConfig.addResponse("Run as different user: 'sudo -u username command'");
    sudoConfig.addResponse("Sudo logs: /var/log/auth.log or 'journalctl SYSLOG_IDENTIFIER=sudo'");
    sudoConfig.addResponse("Remove sudo access: 'sudo deluser username sudo'");
    sudoConfig.setTopic("users");
    rules.add(sudoConfig);

    // USER HOME DIRECTORY
    Rule homeDir = new Rule("home-dir", 80);
    homeDir.addPattern("HOME DIRECTORY");
    homeDir.addPattern("USER FOLDER");
    homeDir.addPattern("HOME FOLDER");
    homeDir.addResponse("Your home: /home/username or ~");
    homeDir.addResponse("Go to home: 'cd' or 'cd ~' or 'cd $HOME'");
    homeDir.addResponse("Another user's home: /home/otherusername (need permissions to access)");
    homeDir.addResponse("Home directory permissions: Usually 755, only owner can write");
    homeDir.addResponse("Hidden files in home: Dotfiles (start with .) - config files, show with 'ls -a'");
    homeDir.addResponse("Change home directory: 'sudo usermod -d /new/home username' (advanced!)");
    homeDir.setTopic("users");
    rules.add(homeDir);
}
    public ConversationContext getContext() {
        return context;
    }

    public LLMConfig getLLMConfig() {
        return llmConfig;
    }
}
