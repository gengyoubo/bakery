# Bakeries Fabric 移植 TODO

对照基准：`bakeries-1.20.1-forge-1.3.6.jar`（Forge 1.3.6）。
最后更新：2026-11-22。

## 已完成（这一轮移植里做过的，供参考）

- [x] 数据目录改回 1.20.1 命名（`advancements` / `loot_tables` / `structures`），进度 JSON 改回 1.20.1 格式
- [x] 所有方块都有掉落表
- [x] 盐矿：普通/深板岩盐矿方块 + 世界生成
- [x] 标签：`bakeries:*`、`c:*`、`minecraft:mineable/*`、`minecraft:music_discs`
- [x] 音效注册 + 字幕翻译；番茄酱粒子（注册 + 客户端渲染）
- [x] 效果：柔软、莓酸、茶涩（及对应食物）
- [x] 面包师村民 + 农民/流浪商人额外交易
- [x] 方块放置朝向改回 Forge 写法，blockstate 旋转与 Forge 一致
- [x] 面包架 / 玻璃面包架 / 咖啡桌 / 沙发左右连接（潜行单放、沙发跨颜色连接）
- [x] 机器分面物品接口（漏斗、Fabric Transfer API）；烤箱/搅拌机/醒发箱打开音效
- [x] 第一批零散内容（27 个物品/方块 + 31 个配方），吐司模具脱模流程
- [x] 烤箱 6 格同时烘烤

## 1. 还没移植的内容（约 70 个注册 ID）

### 蛋糕系统（最大的一块）
- [ ] 蛋糕方块：`cake_base`、`cream_cake`、`multi_layer_cream_cake`、`custom_cake`、`tiramisu`、`pound_cake`、`carrot_cake`、`red_velvet_cake`、`red_velvet_cake_base`、`matcha_cake`、`basque_cake`、`taro_cake`、`niangao_cake`
- [ ] 切块/切片：`cream_cake_cube`、`cut_cake_base`、`soak_coffee_cut_cake_base`、`sliced_pound_cake`
- [ ] 蛋糕卷：`cake_roll`、`cake_roll_processing`、`cream_cake_processing`
- [ ] 纸杯蛋糕：`paper_cup`、`paper_cup_cake_paste`、`cup_cake`
- [ ] 蛋糕盒：`cake_box`（含 `cake_box_bluepink` 变体）
- [ ] 模具与面糊：`mould_two`、`mould_cake_base`/`mould_cake_paste`、`mould_carrot_cake`/`_paste`、`mould_red_velvet_cake`/`_paste`、`mould_matcha_cake`/`_paste`、`mould_pound_cake`/`_paste`、`mould_basque_cake`/`_paste`
- [ ] 液体桶：`cake_paste_bucket`、`egg_yolk_paste_bucket`、`foamed_protein_bucket`
- [ ] 其他：`silicone_paper`、`cocoa_cream`
- [ ] 数据：`data/bakeries/cake_parts/` 共 19 个蛋糕部件
- [ ] 做完后：柔软效果的食物来源（纸杯蛋糕、蛋糕卷、奶油蛋糕块、芋泥蛋糕）

### 石窑 + 披萨
- [ ] `stone_kiln`、`stone_kiln_shovel`
- [ ] `pizza_flatbread`、`raw_pizza`、`pizza`、`sausage_pizza`、`meat_paste_pizza`、`custom_pizza`、`raw_custom_pizza`
- [ ] 配方类型 `bakeries:stone_kiln`（Forge 有 6 个配方：法棍、恰巴塔、乡村面包、佛卡夏、披萨、自定义披萨）
- [ ] 联动标签：`create:passive_boiler_heaters`、`farmersdelight:heat_sources`（都是石窑）

### 月饼
- [ ] `raw_mooncake`、`mooncake`、`raw_pork_mooncake`、`pork_mooncake`、`raw_yuntui_mooncake`、`yuntui_mooncake`、`tray_yuntui_mooncake`
- [ ] 配方类型 `bakeries:mooncake`；圆形模具（`round_mould`，已移植）的实际用途在这里

### 晒盐
- [ ] `baysalt_frame`、`salt_water_bucket`（盐水流体）、`salt_bucket`、`salt_scraper_rake`
- [ ] 粗盐块 / 盐桶相关配方（`raw_salt_block.json`、`salt_bucket.json`）

### 发酵桶
- [ ] `fermentation_barrel` 方块 + 菜单/界面
- [ ] 配方类型 `bakeries:fermentation`（酵母瓶、干酪块、鲜奶酪块）

### 装饰方块
- [ ] `chandelier`、`luminous_light_sign`（也要接入左右连接）、`light_box`、`magnetic_plate`、`glass_cabinet_door_two`、`mix_bread_block`、`bag`

## 2. 和 Forge 行为不一致

- [ ] **食物堆叠放置**：Forge 手持同种面包右键已放置的面包会叠高（`pile` 1→4）；Fabric 的 `FacingPileBlock` 只能放 `pile=1`，没有叠放逻辑
- [ ] **法棍**：Forge 法棍可以吃 4 口，同时是 3 点伤害的武器；Fabric 是普通食物
- [ ] **生面团温度提示**：Forge 的 `RawItem` 会显示"最低 xx°C"；Fabric 生面团是普通物品
- [ ] **饮品**：Forge 饮品可以放到地上（`coldDrinkBlock`），喝完返还饮料杯，喝饮品还能刷新村民交易；Fabric 饮品是普通食物，不能放置
- [ ] **玻璃面包架开门**：Forge 有 `open` 状态和门的渲染
- [ ] **机器动画**：烤箱、醒发箱、搅拌机开门/运转动画（Forge 靠方块实体渲染器）
- [ ] **橱柜**：Fabric 21 格，Forge 27 格（可能是有意改动，待确认）
- [ ] **配置**：Forge 的 `fermentationGameplay` 等配置项；Fabric 写死为 `true`

## 3. 客户端与联动

- [ ] 方块实体渲染器：面包架、玻璃面包架、烤箱、木托盘、搅拌机、醒发箱、蛋糕盒等（目前只有摩卡壶有）
- [ ] JEI：缺石窑、拆模两个分类
- [ ] Jade 插件
- [ ] 内置资源包：`b_2d_icon`、`b_gui`、`b_xiris`（目前只有 `b_16x`）
- [ ] 结构：面包店 `bakery.nbt`
- [ ] Patchouli 手册：缺 8 页（石窑、烤面包机、热源、摩卡壶、蛋糕制作流程等）

## 4. 资源与语言

- [ ] 贴图缺失（Forge jar 里本来就没有）：`textures/item/coarse_salt.png`、`textures/item/pumpkin_stuffing.png`（生南瓜派用）
- [ ] 繁体中文：没有 `zh_tw.json`（Forge 有）
- [ ] zh_cn 还缺约 220 个键，大部分属于未移植内容，随内容一起补
- [ ] lzh：新增物品中有 11 个名称 Forge 也没有，目前回退为英文

## 5. 工程问题

- [ ] `gradle.properties` 的 `org.gradle.java.home` 指向不存在的 `.jdks/temurin17/jdk-17.0.19+10`，直接跑 `gradlew` 会失败（目前需手动指定 `C:/Users/gengy/.jdks/temurin-17.0.21`）
- [ ] 部分文件混合了 LF/CRLF 换行（Git 会提示，不影响运行）
- [ ] `.idea/modules/*.iml` 有 Gradle 同步产生的改动，提交时注意排除

## 6. 待在客户端里确认（服务端测试已通过，但没进游戏看过）

- [ ] 新物品/方块的模型和贴图显示
- [ ] 烤箱每格进度条的位置
- [ ] 番茄酱粒子渲染、各音效播放和字幕
- [ ] 新世界里盐矿的生成
- [ ] 面包师村民就职、贴图、交易
- [ ] 方块放置朝向、沙发/面包架连接的实际观感
