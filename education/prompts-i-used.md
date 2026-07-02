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
テスト追加の計画を立ててほしかったが、テスト追加前の調査計画のような返しがきた。
具体的なテスト追加計画について要求するプロンプトにする必要があったと思う。


## プロンプト X
**フェーズ**:

**プロンプト本文**:

```

```
**結果**: 効かなかった

```

```

**振り返り**:




