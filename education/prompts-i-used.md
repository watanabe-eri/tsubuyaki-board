# 使ったプロンプトと評価

演習中に Codex CLI に投げた主要なプロンプトを記録する。
`EXERCISES.html` に記載の機能実装や、修正提案に関するプロンプトを記述する。

## 書き方

- どのフェーズで使ったか (例: 投稿一覧、いいね機能、リファクタ)
- 実際に投げたプロンプト本文 (省略せずコピペ)
- 結果: 効いた / 部分的に効いた / 効かなかった
- 振り返り: 次に同じ状況が来たらどう変えるか

---

## プロンプト 1

**フェーズ**: S1: いいね機能

**プロンプト本文**:

```
Spring Boot（Thymeleaf + Spring Data JPA + Flyway + Oracle/H2互換）プロジェクトです。

S1機能として「いいね機能」を実装してください。

---

## ■ 目的
投稿（Post）に対してユーザー（clientHash単位）でいいねのON/OFF（トグル）ができるようにする。

---

## ■ 既存前提
- postsテーブルは既に存在
- likesテーブルは既にFlyway V2で作成済み
- likesテーブル構造：
  - id (PK)
  - post_id (FK -> posts.id)
  - client_hash (VARCHAR2(64))
  - created_at (TIMESTAMP)

---

## ■ 実装要件

### 1. Entity
- Likeエンティティを作成
- テーブル：likes
- JPAアノテーションを正しく付与
- Oracle/H2両対応（NUMBER/TIMESTAMP想定）

---

### 2. Repository
以下を実装：
- postId + clientHash で取得するメソッド
- postIdごとのいいね数カウントメソッド

---

### 3. Service
以下のトグル処理を実装：

- 既に (postId, clientHash) が存在する場合 → 削除（いいね解除）
- 存在しない場合 → 新規作成（いいね追加）

トランザクション管理を適切に行うこと。

---

### 4. Controller
- エンドポイント：
  POST /posts/{id}/likes

- 処理：
  - clientHashを生成（requestのIP + UserAgentベース）
  - serviceでトグル処理
  - /posts/{id} にリダイレクト

---

### 5. 画面（Thymeleaf）
- posts/detail.html に以下を追加：
  - いいね数表示
  - いいねボタン（押すとPOST送信）

---

## ■ 重要ルール
- 既存のPost機能は変更しない
- 既存のHTML構造を壊さない
- SQLは変更しない（Flyway済み前提）
- シンプルで研修レベルに合った実装にする
- 過剰設計は禁止（DDDや過度な抽象化は不要）

---

## ■ 出力
- 変更・追加したファイル一覧を必ず出す
- 各ファイルのコードを完全に提示する
```

**結果**: 部分的に効いた

```
• いいね機能を実装しました。

  実装概要

  - Like エンティティを追加
  - LikeRepository を追加
      - findByPostIdAndClientHash
      - countByPostId

  - LikeService を追加
      - 未いいねなら追加
      - いいね済みなら削除

  - POST /posts/{id}/likes を追加
      - IP + UserAgent を SHA-256 化して clientHash 生成
      - toggle 後 /posts/{id} へリダイレクト


  検証

  ./mvnw -B -Ph2 -DforkCount=0 -Dtest=LikeRepositoryTest,LikeServiceTest,PostControllerTest test

  ./mvnw -B -Ph2 -DskipTests checkstyle:check spotbugs:check

  結果: Checkstyle 0件、SpotBugs 0件です。
```

**振り返り**:

いいね機能は実装できたが、トグルボタンになっていなかった。

---

## プロンプト 2

**フェーズ**:いいね機能 いいねボタンのＵＩ改善

**プロンプト本文**:

```
## ■ 目的
いいねボタンのUIを「ハート型のシンプルで可愛いUI」に変更する。

機能は変更せず、見た目のみ改善する。

---

## ■ 重要制約（絶対遵守）
- いいねのロジック（Service / Controller / Repository）は一切変更しない
- DB変更は禁止
- API変更は禁止
- Thymeleafの表示改善のみ
- JavaScript追加は禁止
- 外部ライブラリ追加は禁止

---

## ■ 追加制約（重要）
- ハートは必ずテキスト絵文字を使用する（SVG・画像・アイコンフォントは禁止）
- 未いいねは "♡"（U+2661）固定
- いいね済みは "❤️"（U+2764 FE0F）固定

---

## ■ 実装内容

### 1. UI変更（Thymeleaf）
投稿詳細画面のいいねボタンを以下に変更する：

- 未いいね：♡ いいね（数）
- いいね済み：❤️ いいね済み（数）

※ liked フラグは既に Controller から渡されている前提

---

### 2. CSS追加
ハートボタンをシンプルで可愛いデザインにする：

- pill型（丸みのあるボタン）
- hoverで少し色が変わる
- いいね済みは赤系（強調）
- 未いいねはグレー系（控えめ）

---

### 3. クラス設計
以下のCSSクラスを使用する：

- like-btn（共通スタイル）
- liked（いいね済み時のみ追加）

---

### 4. 表示例

未いいね：
♡ いいね 1

いいね済み：
❤️ いいね済み 1

---

## ■ 出力要件
- 変更したHTML（Thymeleaf）とCSSのみ出力する
- Javaコードは一切変更しない
- 最小変更でUI改善すること
- 既存構造を壊さず差分最小で実装すること
```

**結果**: 効いた
```
• detail.html の「いいね済み」文言を削除しました。

  現在は状態に応じてこう表示されます。

  - 未いいね: ♡ いいね 1
  - いいね済み: ❤️ いいね 1

  Java / Service / Controller / DB は変更していません。
```

**振り返り**:
いいね済みであることが、視覚的にわかりやすくなったので、「いいね済み」の「済み」は削除した。

---

## プロンプト 3

**フェーズ**:検索機能

**プロンプト本文**:

```
## ■ S2の目的
投稿一覧画面を再利用し、
キーワード検索機能を追加する。

---

## ■ 要件

### 1. 検索仕様
- エンドポイント：GET /posts
- クエリパラメータ：q
- 検索対象：投稿本文（content）
- 検索条件：LIKE検索（部分一致）

例：
/posts?q=hello → 本文に "hello" を含む投稿を表示

---

### 2. 画面仕様（Thymeleaf）
一覧画面の上部に検索フォームを追加する：

- input name="q"
- GET送信
- 現在の検索キーワードはinputに保持する
- 検索ボタンあり

---

### 3. 一覧再利用ルール
- 投稿一覧画面（既存テンプレート）をそのまま使用する
- 新規画面は作らない
- 検索結果も同じ一覧テンプレートで表示する

---

## ■ 実装制約（重要）
- 既存の投稿一覧画面の構造は極力変更しない
- Service / Controller / Repository の責務は維持する
- DB変更は禁止
- API設計の大幅変更は禁止
- POST系の既存機能に影響を与えないこと
- JavaScript追加は禁止（HTMLフォームのみで実装）

---

## ■ 実装指針

### Controller
- /posts は GETで q を受け取る
- q が null または空文字の場合は全件取得
- q がある場合のみ検索処理を実行

---

### Repository
- content に対して LIKE 検索を行うメソッドを追加する
- Spring Data JPAの命名規則を使用する（@Queryでも可）
例：
findByContentContaining(String q)

---

### Service
- 全件取得と検索処理を分岐させる

---

### Thymeleaf
- 検索フォームを一覧上部に追加
- GETで /posts?q=xxx に送信
- 入力値は保持する（value="${q}"）

---

## ■ 表示例

検索なし：
/posts → 全投稿表示

検索あり：
/posts?q=spring → "spring"を含む投稿のみ表示

---

## ■ 出力要件
- 変更した Controller / Service / Repository / Thymeleaf のみ出力
- 新規アーキテクチャ追加は禁止
- 最小変更で実装すること
- 既存機能を壊さないこと
```

**結果**: 効いた
```
• 実装しました。変更した実装ファイルは以下です。

  - src/main/java/com/example/tsubuyaki/controller/PostController.java
  - src/main/java/com/example/tsubuyaki/service/PostService.java
  - src/main/java/com/example/tsubuyaki/repository/PostRepository.java
  - src/main/resources/templates/posts/list.html

  内容:

  - GET /posts?q=... を受け取るように変更
  - q が空なら既存の新着50件
  - q があれば body に対する部分一致検索
  - 一覧テンプレート上部に GET 検索フォームを追加
  - 入力値 q を保持

  検証:

  ./mvnw -B -Ph2 -DforkCount=0 -Dtest=PostRepositoryTest,PostServiceTest,PostControllerTest test

  結果: Tests run: 17, Failures: 0, Errors: 0, Skipped: 0

  ./mvnw -B -Ph2 -DskipTests checkstyle:check spotbugs:check

  結果: Checkstyle 0件、SpotBugs 0件です。
```


**振り返り**:
検索処理は問題なく実装できていた。
ワード入力のテキストボックスの下側余白が足りなかったので、ＵＩの指示を追加するとより効果的かもしれない。

---

## プロンプト 4
**フェーズ**:アバター機能

**プロンプト本文**:


```
## ■ S4の目的
投稿に「イニシャル＋カラーアバター」を追加する。

- 投稿者名の頭文字をアバターとして表示
- 背景色はDBに保存した avatar_color を使用
- 視認性の高いSNS風UIにする

---

## ■ 重要制約
- JavaScript追加は禁止
- 外部ライブラリ追加禁止
- 既存機能（投稿・一覧・詳細・いいね）は変更しない
- 最小変更で実装すること

---

## ■ DB変更（必須）
postsテーブルに以下カラムを追加：

- avatar_color VARCHAR2(20)

---

## ■ 保存する値（固定）
以下の文字列で保存する：

- red
- blue
- green
- purple
- gray

---

## ■ 色の対応（重要）
UI表示時は以下のUnicode絵文字も併用して表現すること：

- red    ：🔴（U+1F534）
- blue   ：🔵（U+1F535）
- green  ：🟢（U+1F7E2）
- purple ：🟣（U+1F7E3）
- gray   ：⚪（U+26AA）

※必ずこの対応で固定すること（ブレ禁止）

---

## ■ Entity変更
Postエンティティに追加：
- avatarColor（String）

---

## ■ 実装内容

### 1. 投稿作成フォーム
- アバター色選択UIを追加
- radioボタンで実装
- valueは red/blue/green/purple/gray
- デフォルトは gray

---

### 2. アバター表示（一覧・詳細）
投稿者名の左にアバターを表示する

アバター仕様：
- 円形（circle）
- 背景色：avatarColor
- 中央に投稿者名の頭文字（イニシャル）

表示例：
🔵 T  Taro
🟢 A  Alice

※背景色＋Unicode絵文字＋イニシャルを組み合わせて表示してよい

---

### 3. Thymeleaf
- 投稿一覧・詳細の両方に表示
- nullの場合は gray を使用
- 必ず崩れないようにする

---

## ■ CSS
- .avatar 共通スタイル（円形・中央揃え）
- .avatar-red / blue / green / purple / gray
- 視認性の良いサイズ（32px前後）

---

## ■ Controller / Service
- avatarColor を受け取る
- null時は gray
- 既存ロジック変更禁止

---

## ■ 出力要件
- DB変更SQL
- Entity
- Controller / Service（変更分のみ）
- Thymeleaf（フォーム＋一覧＋詳細）
- CSS
のみ出力すること

---

## ■ ゴール
色・絵文字・イニシャルを組み合わせた直感的なアバターUIを実装すること
```

**結果**: 部分的に効いた

```

• S4 のアバター機能を実装しました。

  変更した主なファイル

  - DB変更SQL: src/main/resources/db/migration/V3__post_avatar_color.sql

    ALTER TABLE posts ADD (
        avatar_color VARCHAR2(20 CHAR)
    );

  - Entity: src/main/java/com/example/tsubuyaki/domain/Post.java
  - Form DTO: src/main/java/com/example/tsubuyaki/web/dto/PostForm.java
  - Controller: src/main/java/com/example/tsubuyaki/controller/PostController.java
  - Service: src/main/java/com/example/tsubuyaki/service/PostService.java
  - Thymeleaf:
      - src/main/resources/templates/posts/form.html
      - src/main/resources/templates/posts/list.html
      - src/main/resources/templates/posts/detail.html

  - CSS: src/main/resources/static/css/app.css

  実装内容は、投稿作成フォームに red / blue / green / purple / gray の radio 選択を追加し、未指定時は gray で保存する形です。一覧・詳細では投稿者名の左
  に、固定対応の絵文字とイニシャル入り円形アバターを表示します。

  検証

  - ./mvnw -B -Ph2 -DforkCount=0 -Dtest=PostFormTest,PostControllerTest,PostServiceTest,PostRepositoryTest test 成功
  - ./mvnw -B -Ph2 -DforkCount=0 verify 成功
      - Tests: 36 件成功
      - Checkstyle: 0 violations
      - SpotBugs: no errors/warnings

  補足: -DforkCount=0 付きのため JaCoCo は実行データなしでスキップされています。既存の未関連変更ファイルは触っていません。
```

**振り返り**:
投稿時のアバター設定、一覧への反映は実装できていたが、
一覧と詳細画面に余分なUnicode絵文字が表示されてしまっていた。


## プロンプト 5
**フェーズ**:カバレッジ テスト拡充

**プロンプト本文**:

```
カバレッジを確認したところ、目標の80%を達成していないことがわかりました。
file:///C:/workspace/tsubuyaki-board/target/site/jacoco/index.html
com.example.tsubuyaki.domain
に対して、カバレッジ80%を達成できるようテストの実装計画を立ててください。
```
**結果**: 効かなかった

```
  ## Verification

  - まず対象テストだけ実行する。

  ./mvnw -B -Ph2 -Dtest=PostTest,LikeTest test

  - その後、JaCoCo レポートを再生成して domain パッケージが 80% 以上になったことを確認する。

  ./mvnw -B -Ph2 verify

  - 仕上げ基準も確認する場合は以下を実行する。

  ./mvnw -B -Ph2 -Pcoverage-day3 -Pstrict verify

  ## Assumptions

  - 80%目標は JaCoCo の domain パッケージ表示に対する目標として扱う。
  - 命令カバレッジと行カバレッジの両方で 80% 以上を狙う。
  - equals / hashCode の検証では、既存テストでも使われている ReflectionTestUtils.setField(..., "id", ...) を使って JPA 採番後の状態を再現する。
```

**振り返り**:
planeモードで実行しちゃ。テスト追加の計画を立ててほしかったが、テスト追加前の調査計画のような返しがきた。
具体的なテスト追加計画について要求するプロンプトにする必要があったと思う。


## プロンプト 6
**フェーズ**:カバレッジ テスト拡充

**プロンプト本文**:

```
com.example.tsubuyakiのカバレッジが80%達成できるよう、テスト追加の実装計画をたててください。
追加予定のテスト件数、テストクラス名を計画に含めてください。
カバレッジの確認はこのコマンドで行ってください。
./mvnw -B -Ph2 -Pcoverage-day3 -Pstrict verify

```
**結果**: 効いた

```
  # com.example.tsubuyaki カバレッジ80%到達計画

  com.example.tsubuyaki パッケージは TsubuyakiApplication だけで構成されており、現状は main メソッドの SpringApplication.run(...) 行が未カバーです。main を直接呼ぶ純 JUnit テ
  ストを 1 件追加し、パッケージの行カバレッジを 80% 以上にします。

  ## Key Changes

  - 追加予定テストクラス: TsubuyakiApplicationMainTest
  - 追加予定テスト件数: 1 件
  - Spring コンテキストは起動しない。
  - Mockito の static mock で SpringApplication.run(TsubuyakiApplication.class, args) を差し替え、main(args) が Spring Boot 起動処理を呼ぶことだけを検証する。
  - 業務コード、設定、依存関係は変更しない。

  ## Test Case

  - アプリ起動_main実行時_SpringApplicationを起動する
      - String[] args = {"--spring.profiles.active=h2"} を用意する。
      - MockedStatic<SpringApplication> で SpringApplication.run(...) を static mock する。
      - TsubuyakiApplication.main(args) を実行する。
      - SpringApplication.run(TsubuyakiApplication.class, args) が 1 回呼ばれたことを検証する。

  ## Verification

  - 指定されたコマンドで確認する。

  ./mvnw -B -Ph2 -Pcoverage-day3 -Pstrict verify
```

**振り返り**:
プロンプト5の結果を受け、追加予定のクラス名、件数を盛り込んだ。



## プロンプト 7
**フェーズ**:カバレッジ テスト拡充

**プロンプト本文**:

```
カバレッジを確認したところ、
com.example.tsubuyaki.serviceの「Missed Branches」が66％でした。
80％超を目標にテストを修正のための実装計画を立ててください。

どのような修正をするか具体的に提示してください。
修正後、カバレッジの確認は不要です。
修正したテストファイルが正常終了することの確認をお願いします。

```
**結果**: 効かなかった

```
```

**振り返り**:
planeモードに切り替えるのを失念しており、実装まで行われてしまった。
「実装計画を立ててください」と明記しても、実装されてしまうことを学習した。



## プロンプト 8
**フェーズ**:C1: タグ機能

**プロンプト本文**:

```
タグ機能を実装します。

まずはデータベース設計としてFlywayマイグレーションを追加してください。

設計方針
- 投稿とタグは多対多とする
- 投稿本文に複数の #tag を含められる
- 同じタグを複数投稿で共有できる

追加するもの
- tagsテーブル
- tags_seq
- post_tagsテーブル

要件
- Oracle XEおよびH2(MODE=Oracle)で動作するDDL
- tags.idはSequence採番
- tags.nameはNOT NULL・UNIQUE
- post_tagsは(post_id, tag_id)を複合主キー
- posts.id、tags.idへの外部キーを設定
- V1__init.sqlの命名規則に合わせる
- Javaコードは変更しない
```
**結果**: 効いた

```
• 提案
  タグ機能用の DB 設計として、Flyway マイグレーション src/main/resources/db/migration/V4__tags.sql:1 を追加しました。Java コードは変更していません。

  根拠
  既存の V1__init.sql / V2__likes.sql に合わせて、Oracle XE 21c と H2 MODE=Oracle で読める NUMBER、VARCHAR2、CREATE SEQUENCE ... NOCACHE 形式にしています。

  差分
  追加内容:

  - tags_seq
  - tags
      - id NUMBER(19) NOT NULL
      - name VARCHAR2(50 CHAR) NOT NULL
      - tags_pk
      - tags_name_uk

  - post_tags
      - post_id, tag_id
      - 複合主キー (post_id, tag_id)
      - posts(id) / tags(id) への外部キー

  - post_tags_tag_id_idx

  確認
  H2 Oracle モードで Flyway マイグレーションが通ることを確認しました。

  ./mvnw -Dtest=H2SeedMigrationTest test

  結果:

  Migrating schema "PUBLIC" to version "4 - tags"
  Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
  BUILD SUCCESS
```

**振り返り**:
プロンプト文をAIに相談しながら組み立てた。
問題なくテーブルが作成された。


## プロンプト 9
**フェーズ**:C1: タグ機能


**プロンプト本文**:

```
Flywayの追加は完了しました。

次にTagエンティティを実装してください。

要件
- tagsテーブルに対応するJPAエンティティ
- 既存のPostエンティティと同じコーディングスタイル
- SequenceGeneratorを使用
- equals()/hashCode()もPostと同じ方針
- getterのみ実装
- コンストラクタの書き方もPostに合わせる

今回はTagエンティティのみ実装してください。
Postとの関連付け(@ManyToMany等)やRepositoryはまだ実装しません。
```
**結果**: 効いた

```
• 提案
  tags テーブルに対応する Tag エンティティを追加しました。

  差分
  追加ファイル:

  - src/main/java/com/example/tsubuyaki/domain/Tag.java:1

  実装内容:

  - @Entity
  - @Table(name = "tags")
  - tags_seq を使う @SequenceGenerator
  - id, name フィールド
  - getter のみ
  - protected Tag() + public Tag(String name)
  - Post と同じ id ベースの equals() / hashCode()

  Post との @ManyToMany 関連や Repository は追加していません。

  確認
  コンパイル確認済みです。

  ./mvnw -DskipTests compile

  結果:

  BUILD SUCCESS

```

**振り返り**:
期待通りに実装できていた。



## プロンプト 10
**フェーズ**:C1: タグ機能

**プロンプト本文**:

```
Tagエンティティの実装が完了しました。

次にTagRepositoryを実装してください。

要件
- JpaRepository<Tag, Long>を継承
- タグ名で検索できるfindByName(String name)を追加
- 必要に応じて一覧取得用メソッドを追加してよい

今回はRepositoryのみ実装してください。
ServiceやController、Postとの関連付けはまだ実装しません。
```

**結果**: 効いた

```

• 提案
  TagRepository を追加しました。今回は指定どおり Repository のみです。

  差分
  追加ファイル:

  - src/main/java/com/example/tsubuyaki/repository/TagRepository.java:1

  実装内容:

  public interface TagRepository extends JpaRepository<Tag, Long> {

      Optional<Tag> findByName(String name);
  }

  一覧取得用メソッドは、現時点で具体的な利用箇所がないため追加していません。JpaRepository 標準の findAll() で足ります。

  確認
  コンパイル確認済みです。

  ./mvnw -DskipTests compile

  結果:

  BUILD SUCCESS
```

**振り返り**:
問題なし。詳細に指示ができた。


## プロンプト 11
**フェーズ**:C1: タグ機能

**プロンプト本文**:

```
タグ機能の続きです。

現在までに以下は実装済みです。
- Flywayで tags / post_tags テーブルを追加
- Tagエンティティ
- TagRepository

次に、post_tags テーブルに対応するJPAエンティティを実装してください。

要件
- post_tags テーブルを表現する PostTag エンティティを作成する
- 複合主キー (post_id, tag_id) をJPAの推奨方法で実装する
- @Embeddable + @EmbeddedId を使用してください
- PostTagId クラスも作成してください
- Post、Tagとは @ManyToOne と @MapsId を使用して関連付けてください
- Post や Tag に @ManyToMany は追加しないでください
- 既存の Post エンティティのコーディングスタイルに合わせてください
- 今回は Entity クラスのみ実装し、RepositoryやService、Controllerはまだ変更しないでください
```
**結果**: 効いた

```
• 提案
  post_tags テーブル用に、複合主キークラスとエンティティを追加しました。Repository / Service / Controller / Post・Tag 側の関連は変更していません。

  差分
  追加ファイル:

  - src/main/java/com/example/tsubuyaki/domain/PostTagId.java:1
      - @Embeddable
      - postId, tagId
      - getter のみ
      - equals() / hashCode()

  - src/main/java/com/example/tsubuyaki/domain/PostTag.java:1
      - @Entity
      - @Table(name = "post_tags")
      - @EmbeddedId
      - Post / Tag へ @ManyToOne(fetch = LAZY) + @MapsId
      - protected no-args constructor + public constructor
      - getter のみ
      - id ベースの equals() / hashCode()

  確認
  コンパイル確認済みです。

  ./mvnw -DskipTests compile

  結果:

  BUILD SUCCESS
```

**振り返り**:
期待通りに実装できていた。


## プロンプト 12
**フェーズ**:C1: タグ機能

**プロンプト本文**:

```
PostTagエンティティの実装が完了しました。

次にPostTagRepositoryを実装してください。

要件
- JpaRepository<PostTag, PostTagId> を継承
- Tagから投稿一覧を取得する際に利用できるメソッドを追加してください
- 投稿保存時にPostTagを登録できるようにしてください
- Spring Data JPAの命名規則を利用してください
- ServiceやControllerはまだ実装しません
```
**結果**: 効いた

```
• 提案
  PostTagRepository を追加しました。Service / Controller は変更していません。

  差分
  追加ファイル:

  - src/main/java/com/example/tsubuyaki/repository/PostTagRepository.java:1
      - JpaRepository<PostTag, PostTagId> を継承
      - findByTag_NameOrderByPost_CreatedAtDesc(String name) を追加

  このメソッドでタグ名から PostTag 一覧を取得し、Service 側で getPost() すればタグ別投稿一覧にできます。投稿保存時の PostTag 登録は JpaRepository 継承の save(PostTag) を使えま
  す。

  確認用に src/test/java/com/example/tsubuyaki/repository/PostTagRepositoryTest.java:1 も追加しました。

  確認
  対象テストは正常終了しました。

  ./mvnw -Dtest=PostTagRepositoryTest test

  結果:

  Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
  BUILD SUCCESS

```

**振り返り**:
期待通りに実装できていた。


## プロンプト 13
**フェーズ**:C1: タグ機能

**プロンプト本文**:

```
次に、投稿本文からタグを抽出する処理を実装してください。

要件
- "#tag" 形式のタグを抽出する
- 同じタグが複数回出現した場合は重複を除去する
- "#" を除いた文字列をタグ名として扱う
- 正規表現を利用して実装する
- この処理はServiceから利用できるようにする
- 今回はタグ抽出ロジックのみ実装し、DB保存処理はまだ実装しない
```
**結果**: 効いた

```
• 提案
  投稿本文からタグ名を抽出する専用部品として TagExtractor を追加しました。DB 保存処理や既存 Service への組み込みはまだ行っていません。

  根拠
  後続で PostService などから利用できるように、src/main/java/com/example/tsubuyaki/service/TagExtractor.java:1 を @Component として実装しています。

  差分
  追加ファイル:

  - src/main/java/com/example/tsubuyaki/service/TagExtractor.java:1
      - 正規表現 #([\p{L}\p{N}_]+) で抽出
      - # を除いたタグ名を返却
      - LinkedHashSet で重複除去と初出順維持
      - null / 空白本文は空リスト

  - src/test/java/com/example/tsubuyaki/service/TagExtractorTest.java:1
      - 複数タグ
      - 重複除去
      - タグなし
      - null
      - 句読点を含めないケース

  確認
  RED として TagExtractor 未定義のコンパイルエラーを確認後、実装して対象テストを通しました。

  ./mvnw -Dtest=TagExtractorTest test

  結果:

  Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
  BUILD SUCCESS
```

**振り返り**:
期待通りに実装できていた。


## プロンプト 14
**フェーズ**:C1: タグ機能

**プロンプト本文**:

```
「タグ名から関連する投稿一覧を取得する」機能を実装してください。

要件
- TagServiceを新規作成する
- PostTagRepositoryを利用する
- タグ名を受け取り、そのタグに紐づく投稿一覧(List<Post>)を返す
- PostTagRepository.findByTag_NameOrderByPost_CreatedAtDesc() を利用する
- PostTagからPostを取り出して返す
- Serviceのみ実装し、ControllerやViewはまだ変更しない
- 既存プロジェクトのコーディングスタイルに合わせる
```

**結果**: 効いた

```
• 提案
  TagService を新規追加し、タグ名から関連投稿一覧を取得できるようにしました。Controller / View は変更していません。

  差分
  追加ファイル:

  - src/main/java/com/example/tsubuyaki/service/TagService.java:1
      - PostTagRepository をコンストラクタ注入
      - findPostsByTagName(String name) を追加
      - findByTag_NameOrderByPost_CreatedAtDesc(name) を利用
      - PostTag::getPost で List<Post> に変換

  - src/test/java/com/example/tsubuyaki/service/TagServiceTest.java:1
      - PostTag から Post を取り出して返すことを検証
      - 該当なしの場合に空リストを返すことを検証

  確認
  RED では TagService 未定義のコンパイルエラーを確認しました。修正後、対象テストは正常終了しています。

  ./mvnw -Dtest=TagServiceTest test

  結果:

  Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
  BUILD SUCCESS

```

**振り返り**:
期待通りに実装できていた。


## プロンプト 15
**フェーズ**:エラー修正

**プロンプト本文**:

```
› 以下のエラー、警告が検出されました。

  [WARN] /mnt/c/workspace/tsubuyaki-board/src/main/java/com/example/tsubuyaki/repository/PostTagRepository.java:13:19: 名前 'findByTag_NameOrderByPost_CreatedAtDesc' はパター
  ン '^[a-z][a-zA-Z0-9]*$' に一致しなければなりません。 [MethodName]
  監査が完了しました。
  [ERROR] Medium: null ではないことがわかっている値 tagsByPostId の冗長な null チェックがあります。com.example.tsubuyaki.controller.PostController.tagsByPostId(List)
  [com.example.tsubuyaki.controller.PostController] 冗長な null チェックが行われている箇所 PostController.java:[line 94] RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE
  [ERROR] Medium: null ではないことがわかっている値 tagsByPostId の冗長な null チェックがあります。com.example.tsubuyaki.controller.TagController.tagsByPostId(List)
  [com.example.tsubuyaki.controller.TagController] 冗長な null チェックが行われている箇所 TagController.java:[line 34] RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE

  これらのエラー解消のための具体的な修正計画をたててください。

  修正するファイル、修正案を提示してください。
```

**結果**: 効いた

```
  # Checkstyle / SpotBugs 警告解消計画

  ## Summary

  品質チェックで検出された3件を、挙動を変えずに解消します。主な方針は、Spring Data JPA の派生クエリ名から _ を除去し、Controller の冗長な null チェックを削除することです。

  ## 修正方針

  - PostTagRepository
      - findByTag_NameOrderByPost_CreatedAtDesc(String name) を findByTagNameOrderByPostCreatedAtDesc(String name) にリネームする。
      - @EntityGraph(attributePaths = "post") は維持する。
      - 戻り値 List<PostTag> と引数 String name は変更しない。
      - Spring Data JPA のプロパティ探索で tag.name と post.createdAt を解決させ、Checkstyle の MethodName ルールに合わせる。

  - TagService

  - Repository / Service テスト
      - PostTagRepositoryTest と TagServiceTest の mock / verify / 呼び出しを新メソッド名に更新する。
      - テストの期待値やデータは変更しない。

  - PostController / TagController
      - tagsByPostId(...) 内の if (tagsByPostId == null) を削除する。
      - return new LinkedHashMap<>(tagService.extractTagsByPostId(posts)); にする。
      - LinkedHashMap へのコピーは維持し、テンプレート側で post.id == null のテストデータでも get(null) が安全に動く状態を保つ。

  ## 修正対象ファイル

  - src/main/java/com/example/tsubuyaki/repository/PostTagRepository.java
  - src/main/java/com/example/tsubuyaki/service/TagService.java
  - src/main/java/com/example/tsubuyaki/controller/PostController.java
  - src/main/java/com/example/tsubuyaki/controller/TagController.java
  - src/test/java/com/example/tsubuyaki/repository/PostTagRepositoryTest.java
  - src/test/java/com/example/tsubuyaki/service/TagServiceTest.java

  ## Test Plan

  - 個別確認:
      - ./mvnw -Dtest=PostTagRepositoryTest test
      - ./mvnw -Dtest=TagServiceTest test
      - ./mvnw -Dtest=PostControllerTest,TagControllerTest test

  - 品質確認:
      - ./mvnw -B -Ph2 verify
      - 仕上げ確認が必要なら ./mvnw -B -Ph2 -Pstrict verify

  ## Assumptions

  - findByTagNameOrderByPostCreatedAtDesc は Spring Data JPA の派生クエリとして PostTag.tag.name と PostTag.post.createdAt を解決する前提。
  - TagService.extractTagsByPostId(posts) は null を返さない設計として扱う。
  - Controller / View の表示仕様、URL、model 属性名は変更しない。

```

**振り返り**:
期待通りに実装できていた。


