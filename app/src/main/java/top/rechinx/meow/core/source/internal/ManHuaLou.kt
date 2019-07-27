package top.rechinx.meow.core.source.internal

import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray

import org.jsoup.Jsoup

import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*


class ManHuaLou:HttpSource() {

    override val name: String = "漫画楼"

    override val baseUrl: String = "https://www.manhualou.com"

    override fun getFilterList(): FilterList
        = FilterList(Types(), Orgins(), Plots(), Letters(), Progresses())

    override fun imageUrlParse(response: Response): String  = throw UnsupportedOperationException("Unused method was called somehow!")

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request{
        if("" != keyword){
            return GET("$baseUrl/search/?keywords=$keyword&page=$page")
        } else{
            val params = filters.map { (it as UriPartFilter).getUrl() }.joinToString("-")
            if("" != params){
                return GET("$baseUrl/list/${params}_$page/")
            }
            return GET("$baseUrl/list_$page/")
        }
    }

    override fun searchMangaParse(response: Response): PagedList<SManga>
            = commonMangaParse(response)

    private fun commonMangaParse(response: Response): PagedList<SManga> {
        val res = response.body()!!.string()
        val ret = Jsoup.parse(res).select("#contList li").map { node -> SManga.create().apply {
            title = node.selectFirst("p").text()
            thumbnail_url  = node.selectFirst("img").attr("src")
            url = node.selectFirst("a").attr("href")
        } }
        return PagedList(ret, true)
    }
    override fun popularMangaRequest(page: Int): Request
            = GET("$baseUrl/list_$page/")

    override fun popularMangaParse(response: Response): PagedList<SManga>
            = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request
            = GET(url)

    override fun mangaInfoParse(response: Response): SManga
            = SManga.create().apply {
        val res = response.body()!!.string()
        val doc = Jsoup.parse(res)
        title = doc.selectFirst(".book-title").text()
        thumbnail_url = doc.selectFirst(".cover .pic").attr("src")
        author = doc.selectFirst("ul.detail-list.cf > li:nth-of-type(2) > span:nth-of-type(2) > a").text()
        status = when(doc.selectFirst(".status a").text()) {
            "已完结" -> SManga.COMPLETED
            "连载中" -> SManga.ONGOING
            else -> SManga.UNKNOWN
        }
        genre = doc.select("ul.detail-list.cf > li:nth-child(2) > span:nth-child(1) > a")
                .map{ node -> node.text() }.joinToString(", ")
        description = doc.selectFirst("#intro-all > p").text()
    }

    override fun chaptersRequest(page: Int, url: String): Request
            = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val res = response.body()!!.string()
        val ret = Jsoup.parse(res).select(".chapter-body.clearfix a").map { node -> SChapter.create().apply {
            name = node.text()
            url = node.attr("href")
        } }
        return PagedList(ret.reversed(), false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request
            = GET(baseUrl + chapter.url)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val res = response.body()!!.string()
        val arr = JSONArray(Regex("chapterImages\\s*=\\s*([^;]*)").find(res)!!.groupValues[1])
        val ret = ArrayList<MangaPage>(arr.length())
        for (i in 0 until arr.length()) {
            ret.add(MangaPage(i, "", "https://restp.dongqiniqin.com/"+arr.getString(i)))
        }
        return ret
    }

    private open class UriPartFilter(displayName: String, val vals: Array<Pair<String, String>>,
                                    defaultValue: Int = 0) :
            Filter.Select<String>(displayName, vals.map { it.first }.toTypedArray(), defaultValue){
        open fun getUrl() = vals[state].second
    }
    private class Types :UriPartFilter("类型", arrayOf(
            Pair("全部", ""),
            Pair("儿童漫画", "ertong"),
            Pair("少年漫画", "shaonian"),
            Pair("少女漫画", "shaonv"),
            Pair("青年漫画", "qingnian")

    ))
    private class Orgins :UriPartFilter("地区", arrayOf(
            Pair("全部", ""),
            Pair("日本", "riben"),
            Pair("大陆", "dalu"),
            Pair("香港", "hongkong"),
            Pair("台湾", "taiwan"),
            Pair("欧美", "oumei"),
            Pair("韩国", "hanguo"),
            Pair("其他", "qita")
    ))
    private class Plots :UriPartFilter("剧情", arrayOf(
            Pair("全部", ""),
            Pair("爱情", "aiqing"),
            Pair("少女爱情", "shaonvaiqing"),
            Pair("欢乐向", "huanlexiang"),
            Pair("耽美", "danmei"),
            Pair("东方", "dongfang"),
            Pair("其他", "qita"),
            Pair("冒险", "maoxian"),
            Pair("奇幻", "qihuan"),
            Pair("性转换", "xingzhuanhuan"),
            Pair("节操", "jiecao"),
            Pair("舰娘", "jianniang"),
            Pair("四格", "sige"),
            Pair("科幻", "kehuan"),
            Pair("校园", "xiaoyuan"),
            Pair("竞技", "jingji"),
            Pair("萌系", "mengxi"),
            Pair("机战", "jizhan"),
            Pair("后宫", "hougong"),
            Pair("格斗", "gedou"),
            Pair("百合", "baihe"),
            Pair("魔幻", "mohuan"),
            Pair("动作格斗", "dongzuogedou"),
            Pair("魔法", "mofa"),
            Pair("生活", "shenghuo"),
            Pair("轻小说", "qingxiaoshuo"),
            Pair("神鬼", "shengui"),
            Pair("悬疑", "xuanyi"),
            Pair("美食", "meishi"),
            Pair("伪娘", "weiniang"),
            Pair("治愈", "zhiyu"),
            Pair("颜艺", "yanyi"),
            Pair("恐怖", "kongbu"),
            Pair("职场", "zhichang"),
            Pair("热血", "rexue"),
            Pair("侦探", "zhentan"),
            Pair("搞笑", "gaoxiao"),
            Pair("音乐舞蹈", "yinyuewudao"),
            Pair("历史", "lishi"),
            Pair("战争", "zhanzheng"),
            Pair("励志", "lizhi"),
            Pair("高清单行", "gaoqingdanxing"),
            Pair("西方魔幻", "xifangmohuan"),
            Pair("宅系", "zhaixi"),
            Pair("魔幻神话", "mohuanshenhua"),
            Pair("校园青春", "xiaoyuanqingchun"),
            Pair("综合其它", "zongheqita"),
            Pair("轻松搞笑", "qingsonggaoxiao"),
            Pair("体育竞技", "tiyujingji"),
            Pair("同人漫画", "tongrenmanhua"),
            Pair("布卡漫画", "bukamanhua"),
            Pair("科幻未来", "kehuanweilai"),
            Pair("悬疑探案", "xuanyitanan"),
            Pair("短篇漫画", "duanpianmanhua"),
            Pair("萌", "meng"),
            Pair("侦探推理", "zhentantuili"),
            Pair("其它漫画", "qitamanhua"),
            Pair("武侠格斗", "wuxiagedou"),
            Pair("科幻魔幻", "kehuanmohuan"),
            Pair("耽美BL", "danmeiBL"),
            Pair("青春", "qingchun"),
            Pair("恋爱", "lianai"),
            Pair("神魔", "shenmo"),
            Pair("恐怖鬼怪", "kongbuguiguai"),
            Pair("青年漫画", "qingnianmanhua"),
            Pair("四格漫画", "sigemanhua"),
            Pair("搞笑喜剧", "gaoxiaoxiju"),
            Pair("玄幻", "xuanhuan"),
            Pair("动作", "dongzuo"),
            Pair("武侠", "wuxia"),
            Pair("穿越", "chuanyue"),
            Pair("同人", "tongren"),
            Pair("架空", "jiakong"),
            Pair("霸总", "bazong"),
            Pair("萝莉", "luoli"),
            Pair("总裁", "zongcai"),
            Pair("古风", "gufeng"),
            Pair("推理", "tuili"),
            Pair("恐怖灵异", "kongbulingyi"),
            Pair("修真", "xiuzhen"),
            Pair("灵异", "lingyi"),
            Pair("真人", "zhenren"),
            Pair("历史漫画", "lishimanhua"),
            Pair("漫改", "mangai"),
            Pair("剧情", "juqing"),
            Pair("美少女", "meishaonv"),
            Pair("故事", "gushi"),
            Pair("都市", "dushi"),
            Pair("社会", "shehui"),
            Pair("竞技体育", "jingjitiyu"),
            Pair("少女", "shaonv"),
            Pair("御姐", "yujie"),
            Pair("运动", "yundong"),
            Pair("杂志", "zazhi"),
            Pair("吸血", "xixie"),
            Pair("泡泡", "paopao"),
            Pair("彩虹", "caihong"),
            Pair("恋爱生活", "lianaishenghuo"),
            Pair("修真热血玄幻", "xiuzhenrexuexuanhuan"),
            Pair("恋爱玄幻", "lianaixuanhuan"),
            Pair("生活悬疑灵异", "shenghuoxuanyilingyi"),
            Pair("霸总生活", "bazongshenghuo"),
            Pair("恋爱生活玄幻", "lianaishenghuoxuanhuan"),
            Pair("架空后宫古风", "jiakonghougonggufeng"),
            Pair("生活悬疑古风", "shenghuoxuanyigufeng"),
            Pair("恋爱热血玄幻", "lianairexuexuanhuan"),
            Pair("恋爱校园生活", "lianaixiaoyuanshenghuo"),
            Pair("玄幻动作", "xuanhuandongzuo"),
            Pair("玄幻科幻", "xuanhuankehuan"),
            Pair("恋爱生活励志", "lianaishenghuolizhi"),
            Pair("悬疑恐怖", "xuanyikongbu"),
            Pair("游戏", "youxi"),
            Pair("恋爱生活科幻", "lianaishenghuokehuan"),
            Pair("修真灵异动作", "xiuzhenlingyidongzuo"),
            Pair("恋爱校园玄幻", "lianaixiaoyuanxuanhuan"),
            Pair("热血动作", "rexuedongzuo"),
            Pair("恋爱科幻", "lianaikehuan"),
            Pair("恋爱搞笑玄幻", "lianaigaoxiaoxuanhuan"),
            Pair("恋爱后宫古风", "lianaihougonggufeng"),
            Pair("恋爱搞笑穿越", "lianaigaoxiaochuanyue"),
            Pair("搞笑热血", "gaoxiaorexue"),
            Pair("修真恋爱架空", "xiuzhenlianaijiakong"),
            Pair("搞笑古风穿越", "gaoxiaogufengchuanyue"),
            Pair("霸总恋爱生活", "bazonglianaishenghuo"),
            Pair("恋爱古风穿越", "lianaigufengchuanyue"),
            Pair("玄幻古风", "xuanhuangufeng"),
            Pair("校园搞笑生活", "xiaoyuangaoxiaoshenghuo"),
            Pair("恋爱校园", "lianaixiaoyuan"),
            Pair("热血玄幻", "rexuexuanhuan"),
            Pair("恋爱生活悬疑", "lianaishenghuoxuanyi"),
            Pair("唯美", "weimei"),
            Pair("霸总恋爱", "bazonglianai"),
            Pair("悬疑动作", "xuanyidongzuo"),
            Pair("搞笑生活", "gaoxiaoshenghuo"),
            Pair("热血架空", "rexuejiakong"),
            Pair("恋爱校园搞笑", "lianaixiaoyuangaoxiao"),
            Pair("校园生活动作", "xiaoyuanshenghuodongzuo"),
            Pair("恋爱搞笑生活", "lianaigaoxiaoshenghuo"),
            Pair("修真热血动作", "xiuzhenrexuedongzuo"),
            Pair("热血玄幻动作", "rexuexuanhuandongzuo"),
            Pair("恋爱搞笑励志", "lianaigaoxiaolizhi"),
            Pair("搞笑生活玄幻", "gaoxiaoshenghuoxuanhuan"),
            Pair("恋爱搞笑科幻", "lianaigaoxiaokehuan"),
            Pair("悬疑古风", "xuanyigufeng"),
            Pair("恋爱架空古风", "lianaijiakonggufeng"),
            Pair("热血科幻战争", "rexuekehuanzhanzheng"),
            Pair("生活悬疑", "shenghuoxuanyi"),
            Pair("修真玄幻", "xiuzhenxuanhuan"),
            Pair("霸总恋爱玄幻", "bazonglianaixuanhuan"),
            Pair("搞笑生活励志", "gaoxiaoshenghuolizhi"),
            Pair("恋爱校园竞技", "lianaixiaoyuanjingji"),
            Pair("冒险热血玄幻", "maoxianrexuexuanhuan"),
            Pair("冒险热血", "maoxianrexue"),
            Pair("恋爱冒险古风", "lianaimaoxiangufeng"),
            Pair("恋爱搞笑古风", "lianaigaoxiaogufeng"),
            Pair("恋爱古风", "lianaigufeng"),
            Pair("霸总恋爱搞笑", "bazonglianaigaoxiao"),
            Pair("恋爱玄幻古风", "lianaixuanhuangufeng"),
            Pair("搞笑生活穿越", "gaoxiaoshenghuochuanyue"),
            Pair("恋爱搞笑后宫", "lianaigaoxiaohougong"),
            Pair("恋爱冒险玄幻", "lianaimaoxianxuanhuan"),
            Pair("恋爱搞笑悬疑", "lianaigaoxiaoxuanyi"),
            Pair("恋爱玄幻穿越", "lianaixuanhuanchuanyue"),
            Pair("生活玄幻", "shenghuoxuanhuan"),
            Pair("校园冒险搞笑", "xiaoyuanmaoxiangaoxiao"),
            Pair("恋爱生活古风", "lianaishenghuogufeng"),
            Pair("恋爱搞笑架空", "lianaigaoxiaojiakong"),
            Pair("冒险热血动作", "maoxianrexuedongzuo"),
            Pair("爆笑", "baoxiao"),
            Pair("热血玄幻悬疑", "rexuexuanhuanxuanyi"),
            Pair("恋爱冒险搞笑", "lianaimaoxiangaoxiao"),
            Pair("修真生活玄幻", "xiuzhenshenghuoxuanhuan"),
            Pair("恋爱悬疑", "lianaixuanyi"),
            Pair("恋爱校园励志", "lianaixiaoyuanlizhi"),
            Pair("修真恋爱古风", "xiuzhenlianaigufeng"),
            Pair("复仇", "fuchou"),
            Pair("虐心", "nuexin"),
            Pair("纯爱", "chunai"),
            Pair("蔷薇", "qiangwei"),
            Pair("震撼", "zhenhan"),
            Pair("惊悚", "jingsong")
    ))
    private class Letters :UriPartFilter("字母", arrayOf(
            Pair("全部", ""),
            Pair("A", "a"),
            Pair("B", "b"),
            Pair("C", "c"),
            Pair("D", "d"),
            Pair("E", "e"),
            Pair("F", "f"),
            Pair("G", "g"),
            Pair("H", "h"),
            Pair("I", "i"),
            Pair("J", "j"),
            Pair("K", "k"),
            Pair("L", "l"),
            Pair("M", "m"),
            Pair("N", "n"),
            Pair("O", "o"),
            Pair("P", "p"),
            Pair("Q", "q"),
            Pair("R", "r"),
            Pair("S", "s"),
            Pair("T", "t"),
            Pair("U", "u"),
            Pair("V", "v"),
            Pair("W", "w"),
            Pair("X", "x"),
            Pair("Y", "y"),
            Pair("Z", "z"),
            Pair("其他", "1")
    ))
    private class Progresses :UriPartFilter("进度", arrayOf(
            Pair("全部", ""),
            Pair("已完结", "wanjie"),
            Pair("连载中", "lianzai")
    ))

}