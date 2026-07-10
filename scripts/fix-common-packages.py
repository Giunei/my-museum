#!/usr/bin/env python3
"""Fix incorrect common.persistence.* packages from repackage bug."""

from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src" / "main" / "java" / "com" / "giunei" / "my_museum"

FIXES: list[tuple[str, str]] = [
    ("com.giunei.my_museum.common.persistence.config", "com.giunei.my_museum.common.config"),
    ("com.giunei.my_museum.common.persistence.websocket", "com.giunei.my_museum.common.websocket"),
    ("com.giunei.my_museum.common.persistence.service", "com.giunei.my_museum.common.storage"),
    ("com.giunei.my_museum.common.persistence.converter", "com.giunei.my_museum.common.persistence"),
]


def package_to_dir(package: str) -> Path:
    rel = package.replace("com.giunei.my_museum.", "").replace(".", "/")
    return JAVA_ROOT / rel


def main() -> None:
    java_files = list(ROOT.rglob("*.java"))

    for path in java_files:
        text = path.read_text(encoding="utf-8")
        updated = text
        for old, new in FIXES:
            updated = updated.replace(old, new)
        if updated != text:
            path.write_text(updated, encoding="utf-8")

    for path in list(JAVA_ROOT.rglob("*.java")):
        match = re.search(r"^package\s+([\w.]+);", path.read_text(encoding="utf-8"), re.MULTILINE)
        if not match:
            continue
        pkg = match.group(1)
        target = package_to_dir(pkg) / path.name
        if path.resolve() != target.resolve():
            target.parent.mkdir(parents=True, exist_ok=True)
            if target.exists():
                target.unlink()
            shutil.move(str(path), str(target))

    for dirpath in sorted(JAVA_ROOT.rglob("*"), reverse=True):
        if dirpath.is_dir() and not any(dirpath.iterdir()):
            dirpath.rmdir()

    # Move JwtAuthenticationEntryPoint to common/security
    entry = JAVA_ROOT / "common" / "config" / "JwtAuthenticationEntryPoint.java"
    if entry.exists():
        dst_dir = JAVA_ROOT / "common" / "security"
        dst_dir.mkdir(parents=True, exist_ok=True)
        text = entry.read_text(encoding="utf-8")
        text = re.sub(
            r"^package\s+[\w.]+;",
            "package com.giunei.my_museum.common.security;",
            text,
            count=1,
            flags=re.MULTILINE,
        )
        text = text.replace("com.giunei.my_museum.common.config", "com.giunei.my_museum.common.security")
        dst = dst_dir / "JwtAuthenticationEntryPoint.java"
        dst.write_text(text, encoding="utf-8")
        entry.unlink()

    # Global import fix for entry point package
    for path in java_files:
        text = path.read_text(encoding="utf-8")
        updated = text.replace(
            "com.giunei.my_museum.common.config.JwtAuthenticationEntryPoint",
            "com.giunei.my_museum.common.security.JwtAuthenticationEntryPoint",
        )
        if updated != text:
            path.write_text(updated, encoding="utf-8")

    print("Common package fix complete.")


if __name__ == "__main__":
    main()
