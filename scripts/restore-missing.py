#!/usr/bin/env python3
"""Restore missing infrastructure classes after repackage."""

from __future__ import annotations

import re
import shutil
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src" / "main" / "java" / "com" / "giunei" / "my_museum"

EXCEPTION_SOURCES = [
    "AccessDeniedException.java",
    "BusinessException.java",
    "DuplicateMediaException.java",
    "EmailDeliveryException.java",
    "EmailNotVerifiedException.java",
    "ErrorResponse.java",
    "ExpiredRefreshTokenException.java",
    "ExpiredTokenException.java",
    "ExternalApiException.java",
    "FileUploadException.java",
    "GlobalExceptionHandler.java",
    "InvalidFileUploadException.java",
    "InvalidMediaRatingException.java",
    "InvalidPasswordOrUsernameException.java",
    "InvalidRefreshTokenException.java",
    "NoAuthenticatedException.java",
    "NotFoundException.java",
    "UserNotFoundException.java",
    "UsernameAlreadyExistsException.java",
]

IMPORT_REPLACEMENTS = [
    ("package com.giunei.my_museum.exceptions;", "package com.giunei.my_museum.common.exception;"),
    ("com.giunei.my_museum.exceptions.", "com.giunei.my_museum.common.exception."),
    ("com.giunei.my_museum.features.", "com.giunei.my_museum."),
    ("com.giunei.my_museum.core.", "com.giunei.my_museum.common."),
    ("com.giunei.my_museum.common.config.", "com.giunei.my_museum.common.config."),
    ("com.giunei.my_museum.book.exeption.", "com.giunei.my_museum.common.exception."),
]


def git_show(path: str) -> str | None:
    rel = f"src/main/java/com/giunei/my_museum/exceptions/{path}"
    for ref in (f":{rel}", f"HEAD:{rel}"):
        result = subprocess.run(
            ["git", "show", ref],
            cwd=ROOT,
            capture_output=True,
            text=True,
        )
        if result.returncode == 0:
            return result.stdout
    return None


def apply_replacements(text: str) -> str:
    for old, new in IMPORT_REPLACEMENTS:
        text = text.replace(old, new)
    # fix accidental double common
    text = text.replace("com.giunei.my_museum.common.common.", "com.giunei.my_museum.common.")
    return text


def write_exception(name: str) -> bool:
    raw = git_show(name)
    if raw is None:
        print(f"SKIP (not in git): {name}")
        return False
    content = apply_replacements(raw)
    if name == "GlobalExceptionHandler.java":
        content = content.replace("HighlightLimitExceededException", "")
        content = re.sub(r"\n\s*@ExceptionHandler\(HighlightLimitExceededException\.class\).*?\n.*?\n.*?\n", "\n", content, flags=re.DOTALL)
    target = JAVA_ROOT / "common" / "exception" / name
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(content, encoding="utf-8")
    print(f"Wrote {target.relative_to(ROOT)}")
    return True


def main() -> None:
    for name in EXCEPTION_SOURCES:
        if name == "HighlightLimitExceededException.java":
            continue
        write_exception(name)

    entity = """package com.giunei.my_museum.common.persistence;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class EntityAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    protected Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
"""
    entity_path = JAVA_ROOT / "common" / "persistence" / "EntityAbstract.java"
    entity_path.parent.mkdir(parents=True, exist_ok=True)
    entity_path.write_text(entity, encoding="utf-8")
    print(f"Wrote {entity_path.relative_to(ROOT)}")

    user_repo = """package com.giunei.my_museum.user.repository;

import com.giunei.my_museum.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchByUsername(@Param("query") String query);
}
"""
    repo_path = JAVA_ROOT / "user" / "repository" / "UserRepository.java"
    repo_path.parent.mkdir(parents=True, exist_ok=True)
    repo_path.write_text(user_repo, encoding="utf-8")
    print(f"Wrote {repo_path.relative_to(ROOT)}")

    app_src = JAVA_ROOT / "com" / "giunei" / "my_museum" / "MyMuseumApplication.java"
    app_dst = JAVA_ROOT / "MyMuseumApplication.java"
    if app_src.exists():
        shutil.move(str(app_src), str(app_dst))
        for d in [JAVA_ROOT / "com"]:
            if d.exists():
                shutil.rmtree(d, ignore_errors=True)
        print("Moved MyMuseumApplication.java")

    # Fix UserMediaService highlight exception import
    media_service = JAVA_ROOT / "media" / "service" / "UserMediaService.java"
    if media_service.exists():
        text = media_service.read_text(encoding="utf-8")
        text = re.sub(r"\nimport com\.giunei\.my_museum\.common\.exception\.HighlightLimitExceededException;", "", text)
        text = re.sub(r"\s*throw new HighlightLimitExceededException\([^)]*\);", "", text)
        media_service.write_text(text, encoding="utf-8")

    print("Restore complete.")


if __name__ == "__main__":
    main()
