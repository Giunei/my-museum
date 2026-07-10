#!/usr/bin/env python3
"""One-shot package migration: features/* -> domain modules, core/exceptions -> common/*."""

from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src" / "main" / "java" / "com" / "giunei" / "my_museum"
TEST_ROOT = ROOT / "src" / "test" / "java" / "com" / "giunei" / "my_museum"

# Longest match first
PACKAGE_RULES: list[tuple[str, str]] = [
    ("com.giunei.my_museum.features.user.profile.comment.", "com.giunei.my_museum.social.comment."),
    ("com.giunei.my_museum.features.user.profile.", "com.giunei.my_museum.profile."),
    ("com.giunei.my_museum.features.user.follow.", "com.giunei.my_museum.social."),
    ("com.giunei.my_museum.features.user.preference.", "com.giunei.my_museum.preference."),
    ("com.giunei.my_museum.features.user.", "com.giunei.my_museum.user."),
    ("com.giunei.my_museum.features.recommendation.", "com.giunei.my_museum.recommendation."),
    ("com.giunei.my_museum.features.achievement.", "com.giunei.my_museum.achievement."),
    ("com.giunei.my_museum.features.integration.", "com.giunei.my_museum.integration."),
    ("com.giunei.my_museum.features.lol.", "com.giunei.my_museum.integration.lol."),
    ("com.giunei.my_museum.features.book.", "com.giunei.my_museum.book."),
    ("com.giunei.my_museum.features.movie.", "com.giunei.my_museum.movie."),
    ("com.giunei.my_museum.features.series.", "com.giunei.my_museum.series."),
    ("com.giunei.my_museum.features.game.", "com.giunei.my_museum.game."),
    ("com.giunei.my_museum.features.media.", "com.giunei.my_museum.media."),
    ("com.giunei.my_museum.features.home.", "com.giunei.my_museum.home."),
    ("com.giunei.my_museum.features.auth.", "com.giunei.my_museum.auth."),
    ("com.giunei.my_museum.core.config.", "com.giunei.my_museum.common.config."),
    ("com.giunei.my_museum.core.websocket.", "com.giunei.my_museum.common.websocket."),
    ("com.giunei.my_museum.core.service.", "com.giunei.my_museum.common.storage."),
    ("com.giunei.my_museum.core.converter.", "com.giunei.my_museum.common.persistence."),
    ("com.giunei.my_museum.core.", "com.giunei.my_museum.common.persistence."),
    ("com.giunei.my_museum.exceptions.", "com.giunei.my_museum.common.exception."),
]

REMOVE_DIRS = [
    JAVA_ROOT / "features" / "museum",
    JAVA_ROOT / "features" / "highlight",
    JAVA_ROOT / "features" / "friendship",
    JAVA_ROOT / "features",
    JAVA_ROOT / "core",
    JAVA_ROOT / "exceptions",
]

FILE_MOVES: list[tuple[str, str]] = [
    ("auth/service/JwtAuthenticationFilter.java", "common/security/JwtAuthenticationFilter.java"),
    ("core/config/SecurityUtils.java", "common/security/SecurityUtils.java"),
    ("user/controller/UserPublicProfileController.java", "profile/controller/UserPublicProfileController.java"),
    ("social/Follow.java", "social/entity/Follow.java"),
    ("social/FollowStatus.java", "social/entity/FollowStatus.java"),
]


def map_package(pkg: str) -> str:
    for old, new in PACKAGE_RULES:
        if pkg == old.rstrip(".") or pkg.startswith(old):
            suffix = pkg[len(old.rstrip(".")) :]
            if suffix and not suffix.startswith("."):
                suffix = "." + suffix.lstrip(".")
            return new.rstrip(".") + suffix
    return pkg


def package_to_dir(package: str) -> Path:
    rel = package.replace("com.giunei.my_museum.", "").replace(".", "/")
    return JAVA_ROOT / rel


def collect_java_files() -> list[Path]:
    files = list(JAVA_ROOT.rglob("*.java"))
    if TEST_ROOT.exists():
        files.extend(TEST_ROOT.rglob("*.java"))
    return files


def apply_replacements(content: str) -> str:
    for old, new in PACKAGE_RULES:
        content = content.replace(old, new)
    return content


def main() -> None:
    java_files = [p for p in JAVA_ROOT.rglob("*.java")]

    # 1) Rewrite package declarations and imports in memory
    updates: dict[Path, tuple[str, str]] = {}
    for path in java_files:
        text = path.read_text(encoding="utf-8")
        match = re.search(r"^package\s+([\w.]+);", text, re.MULTILINE)
        if not match:
            continue
        old_pkg = match.group(1)
        new_pkg = map_package(old_pkg)
        new_text = apply_replacements(text)
        if old_pkg != new_pkg:
            new_text = re.sub(
                r"^package\s+[\w.]+;",
                f"package {new_pkg};",
                new_text,
                count=1,
                flags=re.MULTILINE,
            )
        updates[path] = (text, new_text)

    # 2) Write updated content
    for path, (_, new_text) in updates.items():
        path.write_text(new_text, encoding="utf-8")

    # 3) Move files to new directory layout
    for path in list(JAVA_ROOT.rglob("*.java")):
        match = re.search(r"^package\s+([\w.]+);", path.read_text(encoding="utf-8"), re.MULTILINE)
        if not match:
            continue
        new_pkg = match.group(1)
        target_dir = package_to_dir(new_pkg)
        target_dir.mkdir(parents=True, exist_ok=True)
        target = target_dir / path.name
        if path.resolve() != target.resolve():
            if target.exists():
                target.unlink()
            shutil.move(str(path), str(target))

    # 4) Extra file moves (security, public profile controller, follow entities)
    for rel_old, rel_new in FILE_MOVES:
        src = JAVA_ROOT / Path(rel_old.replace("/", "\\") if "\\" in str(JAVA_ROOT) else rel_old)
        if not src.exists():
            # try alternate path after step 3
            name = Path(rel_old).name
            found = list(JAVA_ROOT.rglob(name))
            src = found[0] if found else src
        dst = JAVA_ROOT / rel_new.replace("/", "\\") if "\\" in str(JAVA_ROOT) else JAVA_ROOT / rel_new
        if src.exists():
            dst.parent.mkdir(parents=True, exist_ok=True)
            text = apply_replacements(src.read_text(encoding="utf-8"))
            new_pkg = ".".join(["com", "giunei", "my_museum"] + list(Path(rel_new).parent.parts))
            text = re.sub(r"^package\s+[\w.]+;", f"package {new_pkg};", text, count=1, flags=re.MULTILINE)
            dst.write_text(text, encoding="utf-8")
            if src.resolve() != dst.resolve() and src.exists():
                src.unlink()

    # 5) Update test sources
    if TEST_ROOT.exists():
        for path in TEST_ROOT.rglob("*.java"):
            text = apply_replacements(path.read_text(encoding="utf-8"))
            path.write_text(text, encoding="utf-8")

    # 6) Remove empty legacy trees and dead modules
    for d in REMOVE_DIRS:
        if d.exists():
            shutil.rmtree(d, ignore_errors=True)

    # prune empty directories
    for base in [JAVA_ROOT, TEST_ROOT]:
        if not base.exists():
            continue
        for dirpath in sorted(base.rglob("*"), reverse=True):
            if dirpath.is_dir() and not any(dirpath.iterdir()):
                dirpath.rmdir()

    print("Repackage complete.")


if __name__ == "__main__":
    main()
