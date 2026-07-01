package com.example.tsubuyaki.web.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PostFormTest {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    @AfterAll
    static void closeValidatorFactory() {
        VALIDATOR_FACTORY.close();
    }

    @Test
    @DisplayName("投稿フォーム_投稿者と本文が最小文字数の場合_バリデーションエラーにならない")
    void 投稿フォーム_投稿者と本文が最小文字数の場合_バリデーションエラーにならない() {
        PostForm form = new PostForm();
        form.setAuthor("a");
        form.setBody("b");

        Set<ConstraintViolation<PostForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("投稿フォーム_初期状態の場合_アバター色はblueになる")
    void 投稿フォーム_初期状態の場合_アバター色はblueになる() {
        PostForm form = new PostForm();

        assertThat(form.getAvatarColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("投稿フォーム_投稿者と本文が最大文字数の場合_バリデーションエラーにならない")
    void 投稿フォーム_投稿者と本文が最大文字数の場合_バリデーションエラーにならない() {
        PostForm form = new PostForm();
        form.setAuthor("a".repeat(30));
        form.setBody("b".repeat(280));

        Set<ConstraintViolation<PostForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("投稿フォーム_アバター色がyellowの場合_バリデーションエラーにならない")
    void 投稿フォーム_アバター色がyellowの場合_バリデーションエラーにならない() {
        PostForm form = new PostForm();
        form.setAuthor("alice");
        form.setBody("共有事項があります");
        form.setAvatarColor("yellow");

        Set<ConstraintViolation<PostForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("投稿フォーム_アバター色がorangeの場合_バリデーションエラーにならない")
    void 投稿フォーム_アバター色がorangeの場合_バリデーションエラーにならない() {
        PostForm form = new PostForm();
        form.setAuthor("alice");
        form.setBody("共有事項があります");
        form.setAvatarColor("orange");

        Set<ConstraintViolation<PostForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("投稿フォーム_投稿者と本文が空白のみの場合_NotBlankエラーになる")
    void 投稿フォーム_投稿者と本文が空白のみの場合_NotBlankエラーになる() {
        PostForm form = new PostForm();
        form.setAuthor("   ");
        form.setBody("   ");

        Set<ConstraintViolation<PostForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).extracting(violation -> violation.getPropertyPath().toString())
                .contains("author", "body");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("投稿者名を入力してください", "本文を入力してください");
    }

    @Test
    @DisplayName("投稿フォーム_投稿者と本文が最大文字数を超える場合_Sizeエラーになる")
    void 投稿フォーム_投稿者と本文が最大文字数を超える場合_Sizeエラーになる() {
        PostForm form = new PostForm();
        form.setAuthor("a".repeat(31));
        form.setBody("b".repeat(281));

        Set<ConstraintViolation<PostForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).extracting(violation -> violation.getPropertyPath().toString())
                .contains("author", "body");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("投稿者名は 30 文字以内で入力してください", "本文は 280 文字以内で入力してください");
    }

    @Test
    @DisplayName("投稿フォーム_アバター色が許可値以外の場合_Patternエラーになる")
    void 投稿フォーム_アバター色が許可値以外の場合_Patternエラーになる() {
        PostForm form = new PostForm();
        form.setAuthor("alice");
        form.setBody("共有事項があります");
        form.setAvatarColor("black");

        Set<ConstraintViolation<PostForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).extracting(violation -> violation.getPropertyPath().toString())
                .contains("avatarColor");
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("アバター色を選択してください");
    }
}
