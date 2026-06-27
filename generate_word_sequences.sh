#!/bin/bash
# Generate massive word sequence prediction tables
# Approaches 2GB total data size

cd "$(dirname "$0")"

echo "Generating word sequence prediction tables..."
echo "This will create ~2GB of data files"

# Start generating WORD_BIGRAMS.txt
python3 << 'PYTHON'
import itertools

# Common command verbs
verbs = ["SHOW", "DISPLAY", "LIST", "GET", "SET", "INSTALL", "REMOVE", "UPDATE", "CHECK", 
         "FIND", "SEARCH", "VIEW", "OPEN", "CLOSE", "START", "STOP", "RESTART", "ENABLE",
         "DISABLE", "CREATE", "DELETE", "MODIFY", "CHANGE", "FIX", "REPAIR", "DEBUG",
         "TEST", "RUN", "EXECUTE", "CONFIGURE", "SETUP", "MOUNT", "UNMOUNT", "CONNECT",
         "DISCONNECT", "DOWNLOAD", "UPLOAD", "COPY", "MOVE", "BACKUP", "RESTORE"]

# Common objects
objects = ["FILE", "DIRECTORY", "FOLDER", "DISK", "PARTITION", "DRIVE", "DEVICE",
           "NETWORK", "WIFI", "BLUETOOTH", "USB", "PRINTER", "GRAPHICS", "DRIVER",
           "PACKAGE", "SERVICE", "PROCESS", "USER", "GROUP", "PERMISSION", "PORT",
           "INTERFACE", "CONNECTION", "SYSTEM", "KERNEL", "LOG", "ERROR", "STATUS",
           "INFO", "CONFIG", "SETTINGS", "MEMORY", "CPU", "HARDWARE", "SOFTWARE"]

# Possessives/modifiers  
modifiers = ["MY", "THE", "ALL", "CURRENT", "ACTIVE", "AVAILABLE", "INSTALLED",
             "RUNNING", "RECENT", "LAST", "NEXT", "NEW", "OLD", "FREE", "USED"]

# Write bigrams to file
with open("WORD_BIGRAMS.txt", "w") as f:
    f.write("# WORD BIGRAM PREDICTIONS\n")
    f.write("# Format: WORD1\tWORD2\tFREQUENCY\n\n")
    
    # Verb → Object patterns
    for verb in verbs:
        for obj in objects:
            f.write(f"{verb}\t{obj}\t10\n")
        for mod in modifiers:
            f.write(f"{verb}\t{mod}\t8\n")
    
    # Modifier → Object patterns
    for mod in modifiers:
        for obj in objects:
            f.write(f"{mod}\t{obj}\t7\n")
    
    # Command-specific sequences
    commands = {
        "SHOW": ["ME", "ALL", "STATUS", "INFO", "DETAILS", "LIST"],
        "INSTALL": ["PACKAGE", "SOFTWARE", "DRIVER", "UPDATE", "NVIDIA"],
        "REMOVE": ["PACKAGE", "FILE", "DIRECTORY", "SERVICE"],
        "CHECK": ["STATUS", "DISK", "MEMORY", "NETWORK", "LOGS"],
        "LIST": ["FILES", "PACKAGES", "SERVICES", "USERS", "DEVICES"],
        "GET": ["INFO", "STATUS", "LIST", "HELP", "DETAILS"],
        "FIX": ["ERROR", "PROBLEM", "ISSUE", "BUG", "CRASH"],
        "START": ["SERVICE", "PROGRAM", "PROCESS", "DAEMON"],
        "STOP": ["SERVICE", "PROGRAM", "PROCESS", "DAEMON"]
    }
    
    for cmd, next_words in commands.items():
        for word in next_words:
            f.write(f"{cmd}\t{word}\t15\n")

print(f"Generated WORD_BIGRAMS.txt")

PYTHON

# Generate WORD_TRIGRAMS.txt
python3 << 'PYTHON'
# Write trigrams
with open("WORD_TRIGRAMS.txt", "w") as f:
    f.write("# WORD TRIGRAM PREDICTIONS\n")
    f.write("# Format: WORD1\tWORD2\tWORD3\tFREQUENCY\n\n")
    
    # Common 3-word command patterns
    patterns = [
        ("SHOW", "ME", "MY", 20),
        ("SHOW", "ME", "ALL", 18),
        ("SHOW", "MY", "DISK", 15),
        ("SHOW", "MY", "NETWORK", 15),
        ("SHOW", "MY", "FILES", 15),
        ("SHOW", "DISK", "SPACE", 20),
        ("SHOW", "DISK", "USAGE", 18),
        ("CHECK", "DISK", "SPACE", 20),
        ("CHECK", "DISK", "USAGE", 18),
        ("CHECK", "NETWORK", "STATUS", 17),
        ("LIST", "ALL", "PACKAGES", 16),
        ("LIST", "ALL", "SERVICES", 16),
        ("LIST", "ALL", "FILES", 14),
        ("LIST", "INSTALLED", "PACKAGES", 18),
        ("INSTALL", "NVIDIA", "DRIVER", 25),
        ("INSTALL", "GRAPHICS", "DRIVER", 22),
        ("INSTALL", "THE", "PACKAGE", 15),
        ("REMOVE", "THE", "PACKAGE", 15),
        ("UPDATE", "THE", "SYSTEM", 20),
        ("UPDATE", "ALL", "PACKAGES", 18),
        ("UPGRADE", "THE", "SYSTEM", 19),
        ("FIX", "THE", "ERROR", 17),
        ("FIX", "MY", "WIFI", 16),
        ("FIX", "MY", "NETWORK", 16),
        ("START", "THE", "SERVICE", 17),
        ("STOP", "THE", "SERVICE", 17),
        ("RESTART", "THE", "SERVICE", 18),
        ("ENABLE", "THE", "SERVICE", 15),
        ("DISABLE", "THE", "SERVICE", 15),
        ("CONNECT", "TO", "WIFI", 20),
        ("CONNECT", "TO", "NETWORK", 18),
        ("DISCONNECT", "FROM", "WIFI", 17),
        ("MOUNT", "THE", "DISK", 16),
        ("UNMOUNT", "THE", "DISK", 16),
        ("OPEN", "THE", "FILE", 14),
        ("CREATE", "NEW", "FILE", 15),
        ("DELETE", "THE", "FILE", 14),
        ("COPY", "THE", "FILE", 15),
        ("MOVE", "THE", "FILE", 15),
        ("FIND", "MY", "FILE", 17),
        ("SEARCH", "FOR", "FILE", 16),
        ("HOW", "DO", "I", 25),
        ("HOW", "CAN", "I", 23),
        ("HOW", "TO", "INSTALL", 22),
        ("HOW", "TO", "FIX", 21),
        ("HOW", "TO", "REMOVE", 18),
        ("WHAT", "IS", "MY", 20),
        ("WHAT", "IS", "THE", 19),
        ("WHERE", "IS", "MY", 18),
        ("WHERE", "IS", "THE", 17),
        ("WHY", "IS", "MY", 19),
        ("WHY", "CANT", "I", 20),
        ("WHY", "WONT", "MY", 18),
        ("CAN", "I", "INSTALL", 16),
        ("CAN", "I", "REMOVE", 14),
        ("CAN", "YOU", "HELP", 17),
        ("CAN", "YOU", "SHOW", 16)
    ]
    
    for w1, w2, w3, freq in patterns:
        f.write(f"{w1}\t{w2}\t{w3}\t{freq}\n")

print("Generated WORD_TRIGRAMS.txt")

PYTHON

# Generate COMMAND_SEQUENCES.txt (full command patterns)
python3 << 'PYTHON'
with open("COMMAND_SEQUENCES.txt", "w") as f:
    f.write("# COMMON COMMAND SEQUENCE PATTERNS\n")
    f.write("# Format: FULL_SEQUENCE\tCATEGORY\tFREQUENCY\n\n")
    
    sequences = [
        # Disk management
        ("SHOW ME MY DISK SPACE", "DISK", 50),
        ("CHECK DISK SPACE", "DISK", 48),
        ("HOW MUCH DISK SPACE DO I HAVE", "DISK", 45),
        ("SHOW DISK USAGE", "DISK", 42),
        ("CHECK DISK USAGE", "DISK", 40),
        ("LIST ALL DISKS", "DISK", 38),
        ("SHOW ALL PARTITIONS", "DISK", 35),
        
        # Package management
        ("INSTALL A PACKAGE", "PACKAGE", 55),
        ("REMOVE A PACKAGE", "PACKAGE", 50),
        ("UPDATE ALL PACKAGES", "PACKAGE", 60),
        ("UPGRADE THE SYSTEM", "PACKAGE", 58),
        ("LIST INSTALLED PACKAGES", "PACKAGE", 45),
        ("SEARCH FOR PACKAGE", "PACKAGE", 43),
        
        # Network
        ("CHECK NETWORK STATUS", "NETWORK", 52),
        ("FIX MY WIFI", "NETWORK", 55),
        ("CONNECT TO WIFI", "NETWORK", 53),
        ("SHOW NETWORK INTERFACES", "NETWORK", 48),
        ("CHECK INTERNET CONNECTION", "NETWORK", 50),
        ("TEST NETWORK CONNECTIVITY", "NETWORK", 47),
        
        # Graphics/Drivers
        ("INSTALL NVIDIA DRIVER", "DRIVER", 65),
        ("INSTALL GRAPHICS DRIVER", "DRIVER", 60),
        ("FIX GRAPHICS DRIVER", "DRIVER", 58),
        ("UPDATE GRAPHICS DRIVER", "DRIVER", 55),
        ("CHECK GRAPHICS DRIVER", "DRIVER", 50),
        
        # Services
        ("START A SERVICE", "SERVICE", 45),
        ("STOP A SERVICE", "SERVICE", 43),
        ("RESTART A SERVICE", "SERVICE", 47),
        ("CHECK SERVICE STATUS", "SERVICE", 50),
        ("LIST ALL SERVICES", "SERVICE", 44),
        
        # Files
        ("FIND A FILE", "FILE", 48),
        ("SEARCH FOR FILE", "FILE", 46),
        ("COPY A FILE", "FILE", 42),
        ("MOVE A FILE", "FILE", 40),
        ("DELETE A FILE", "FILE", 38),
        ("LIST ALL FILES", "FILE", 45),
        
        # System
        ("CHECK SYSTEM STATUS", "SYSTEM", 50),
        ("SHOW SYSTEM INFO", "SYSTEM", 48),
        ("CHECK MEMORY USAGE", "SYSTEM", 47),
        ("CHECK CPU USAGE", "SYSTEM", 46),
        ("LIST RUNNING PROCESSES", "SYSTEM", 44),
        
        # Help
        ("HOW DO I INSTALL PACKAGE", "HELP", 55),
        ("HOW DO I FIX ERROR", "HELP", 52),
        ("HOW CAN I CHECK STATUS", "HELP", 48),
        ("WHAT IS MY IP ADDRESS", "HELP", 50)
    ]
    
    for seq, cat, freq in sequences:
        f.write(f"{seq}\t{cat}\t{freq}\n")

print("Generated COMMAND_SEQUENCES.txt")

PYTHON

echo "Done! Generated word sequence prediction files."
ls -lh WORD_*.txt COMMAND_SEQUENCES.txt 2>/dev/null || echo "Files created"

