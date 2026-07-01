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

**振り返り**:
いいね済みであることが、視覚的にわかりやすくなったので、「いいね済み」の「済み」は削除した。

---

## プロンプト 3

**フェーズ**:

**プロンプト本文**:

```

```

**結果**: 効いた / 部分的に効いた / 効かなかった

**振り返り**:

---

## プロンプト 4 以降

3 件目以降も追加可能。書ければ書くほど良い。
