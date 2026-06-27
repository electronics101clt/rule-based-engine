package com.eliza;

import java.util.*;

/**
 * Ubuntu Assistant - Rule-based conversational helper for Linux/Ubuntu
 */
public class UbuntuAssistant {

    private List<Rule> rules;
    private ConversationContext context;
    private Random random;

    public UbuntuAssistant() {
        this.rules = new ArrayList<>();
        this.context = new ConversationContext();
        this.random = new Random();
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
            response = getDefaultResponse();
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

    public ConversationContext getContext() {
        return context;
    }
}
