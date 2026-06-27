#!/bin/bash
# Generate full 2GB vocabulary for Ubuntu flavor
# Current: 59 MB → Target: 2048 MB

echo "===================================================================="
echo "ELIZA Ubuntu Flavor - Full 2GB Vocabulary Generator"
echo "===================================================================="
echo

cd data

TARGET_BYTES=$((2 * 1024 * 1024 * 1024))  # 2 GB
echo "Target size: 2048 MB"
echo "Current size: $(du -sh . | awk '{print $1}')"
echo

python3 << 'PYTHON'
import os
import sys

print("Phase 1: Massively expanding WORD_BIGRAMS.txt...")
print("=" * 60)

target = 2 * 1024 * 1024 * 1024  # 2 GB
current = sum(os.path.getsize(f) for f in os.listdir('.') if os.path.isfile(f))

print(f"Current: {current / (1024*1024):.1f} MB")
print(f"Target: {target / (1024*1024):.1f} MB")
print(f"Need: {(target - current) / (1024*1024):.1f} MB more")
print()

# Load all packages
packages = []
if os.path.exists("../../dictionaries/all-package-names.txt"):
    with open("../../dictionaries/all-package-names.txt") as f:
        packages = [line.strip().upper() for line in f if line.strip()]
print(f"Packages: {len(packages)}")

# Generate ALL combinations for WORD_BIGRAMS.txt
print("Expanding WORD_BIGRAMS.txt with all package combinations...")

actions = [
    "INSTALL", "REMOVE", "UPDATE", "UPGRADE", "PURGE", "REINSTALL",
    "SEARCH", "SHOW", "INFO", "LIST", "DOWNLOAD", "SOURCE",
    "BUILD", "CHECK", "VERIFY", "FIX", "CONFIGURE", "SETUP",
    "ENABLE", "DISABLE", "START", "STOP", "RESTART", "RELOAD",
    "STATUS", "TEST", "DEBUG", "TRACE", "LOG", "MONITOR",
    "GET", "SET", "ADD", "DELETE", "MODIFY", "CHANGE",
    "BACKUP", "RESTORE", "EXPORT", "IMPORT", "SYNC", "CLONE"
]

with open("WORD_BIGRAMS.txt", "a") as f:
    f.write("\n# ===== PHASE 1: ALL PACKAGE ACTIONS =====\n")
    
    count = 0
    for pkg in packages:
        for action in actions:
            f.write(f"{action}\t{pkg}\t10\n")
            count += 1
            if count % 500000 == 0:
                current = sum(os.path.getsize(ff) for ff in os.listdir('.') if os.path.isfile(ff))
                print(f"  {count:,} lines... {current/(1024*1024):.1f} MB")
                sys.stdout.flush()

print(f"Added {count:,} package action combinations")

current = sum(os.path.getsize(f) for f in os.listdir('.') if os.path.isfile(f))
print(f"Current size: {current/(1024*1024):.1f} MB")
print()

PYTHON

echo
echo "Checking size..."
du -sh .


echo
echo "Phase 2: Generating comprehensive package descriptions..."
python3 << 'PYTHON'
import os
import sys

packages = []
if os.path.exists("../../dictionaries/all-package-names.txt"):
    with open("../../dictionaries/all-package-names.txt") as f:
        packages = [line.strip().upper() for line in f if line.strip()]

print(f"Generating detailed responses for {len(packages)} packages...")

# Generate comprehensive package information
with open("ELIZA_DATA.txt", "a") as f:
    f.write("\n# ===== PHASE 2: COMPREHENSIVE PACKAGE DESCRIPTIONS =====\n\n")
    
    count = 0
    for pkg in packages:
        # Multiple response templates for each package
        responses = [
            f"Package {pkg}: Install with 'sudo apt install {pkg}'",
            f"To install {pkg}: sudo apt install {pkg}",
            f"Remove {pkg}: sudo apt remove {pkg} or sudo apt purge {pkg}",
            f"Get {pkg} info: apt show {pkg} or apt-cache show {pkg}",
            f"Search for {pkg}: apt search {pkg}",
            f"{pkg} dependencies: apt-cache depends {pkg}",
            f"{pkg} reverse dependencies: apt-cache rdepends {pkg}",
            f"Download {pkg} without installing: apt download {pkg}",
            f"Reinstall {pkg}: sudo apt install --reinstall {pkg}",
            f"Check if {pkg} is installed: dpkg -l | grep {pkg}",
        ]
        
        f.write(f"{pkg}\t" + "\t".join(responses) + "\n")
        count += 1
        
        if count % 10000 == 0:
            current = sum(os.path.getsize(ff) for ff in os.listdir('.') if os.path.isfile(ff))
            print(f"  {count:,} packages... {current/(1024*1024):.1f} MB")
            sys.stdout.flush()

current = sum(os.path.getsize(f) for f in os.listdir('.') if os.path.isfile(f))
print(f"Added {count:,} package descriptions")
print(f"Current size: {current/(1024*1024):.1f} MB")

PYTHON

echo
du -sh data/
