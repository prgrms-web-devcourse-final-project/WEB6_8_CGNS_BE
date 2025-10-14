package com.back.koreaTravelGuide.domain.user.enums

enum class Region(val displayName: String) {
    // 서울특별시
    SEOUL("서울"),

    // 부산광역시
    BUSAN("부산"),

    // 대구광역시
    DAEGU("대구"),

    // 인천광역시
    INCHEON("인천"),
    BAENGNYEONG("백령도"),
    GANGHWA("강화"),

    // 광주광역시
    GWANGJU("광주"),

    // 대전광역시
    DAEJEON("대전"),

    // 울산광역시
    ULSAN("울산"),

    // 세종특별자치시
    SEJONG("세종"),

    // 경기도
    GWACHEON("과천"),
    GWANGMYEONG("광명"),
    GIMPO("김포"),
    SIHEUNG("시흥"),
    ANSAN("안산"),
    BUCHEON("부천"),
    UIJEONGBU("의정부"),
    GOYANG("고양"),
    YANGJU("양주"),
    PAJU("파주"),
    DONGDUCHEON("동두천"),
    YEONCHEON("연천"),
    POCHEON("포천"),
    GAPYEONG("가평"),
    GURI("구리"),
    NAMYANGJU("남양주"),
    YANGPYEONG("양평"),
    HANAM("하남"),
    SUWON("수원"),
    ANYANG("안양"),
    OSAN("오산"),
    HWASEONG("화성"),
    SEONGNAM("성남"),
    PYEONGTAEK("평택"),
    UIWANG("의왕"),
    GUNPO("군포"),
    ANSEONG("안성"),
    YONGIN("용인"),
    ICHEON("이천"),
    YEOJU("여주"),

    // 강원특별자치도
    CHEORWON("철원"),
    HWACHEON("화천"),
    INJE("인제"),
    YANGGU("양구"),
    CHUNCHEON("춘천"),
    HONGCHEON("홍천"),
    WONJU("원주"),
    HOENGSEONG("횡성"),
    YEONGWOL("영월"),
    JEONGSEON("정선"),
    PYEONGCHANG("평창"),
    DAEGWALLYEONG("대관령"),
    TAEBAEK("태백"),
    SOKCHO("속초"),
    YANGYANG("양양"),
    GANGNEUNG("강릉"),
    DONGHAE("동해"),
    SAMCHEOK("삼척"),

    // 충청북도
    CHUNGJU("충주"),
    JINCHEON("진천"),
    EUMSEONG("음성"),
    JECHEON("제천"),
    DANYANG("단양"),
    CHEONGJU("청주"),
    BOEUN("보은"),
    GOESAN("괴산"),
    JEUNGPYEONG("증평"),
    CHUPUNGNYEONG("추풍령"),
    YEONGDONG("영동"),
    OKCHEON("옥천"),

    // 충청남도
    SEOSAN("서산"),
    TAEAN("태안"),
    DANGJIN("당진"),
    HONGSEONG("홍성"),
    BORYEONG("보령"),
    SEOCHEON("서천"),
    CHEONAN("천안"),
    ASAN("아산"),
    YESAN("예산"),
    GONGJU("공주"),
    GYERYONG("계룡"),
    BUYEO("부여"),
    CHEONGYANG("청양"),
    GEUMSAN("금산"),
    NONSAN("논산"),

    // 전북특별자치도
    JEONJU("전주"),
    IKSAN("익산"),
    JEONGEUP("정읍"),
    WANJU("완주"),
    JANGSU("장수"),
    MUJU("무주"),
    JINAN("진안"),
    NAMWON("남원"),
    IMSIL("임실"),
    SUNCHANG("순창"),
    GUNSAN("군산"),
    GIMJE("김제"),
    GOCHANG("고창"),
    BUAN("부안"),

    // 전라남도
    HAMPYEONG("함평"),
    YEONGGWANG("영광"),
    JINDO("진도"),
    WANDO("완도"),
    HAENAM("해남"),
    GANGJIN("강진"),
    JANGHEUNG("장흥"),
    YEOSU("여수"),
    GWANGYANG("광양"),
    GOHEUNG("고흥"),
    BOSEONG("보성"),
    SUNCHEON("순천"),
    JANGSEONG("장성"),
    NAJU("나주"),
    DAMYANG("담양"),
    HWASUN("화순"),
    GURYE("구례"),
    GOKSEONG("곡성"),
    HEUKSANDO("흑산도"),
    MOKPO("목포"),
    YEONGAM("영암"),
    SINAN("신안"),
    MUAN("무안"),

    // 경상북도
    ULLEUNGDO("울릉도"),
    DOKDO("독도"),
    ULJIN("울진"),
    YEONGDEOK("영덕"),
    POHANG("포항"),
    GYEONGJU("경주"),
    MUNGYEONG("문경"),
    SANGJU("상주"),
    YECHEON("예천"),
    YEONGJU("영주"),
    BONGHWA("봉화"),
    YEONGYANG("영양"),
    ANDONG("안동"),
    UISEONG("의성"),
    CHEONGSONG("청송"),
    GIMCHEON("김천"),
    GUMI("구미"),
    GUNWI("군위"),
    GORYEONG("고령"),
    SEONGJU("성주"),
    YEONGCHEON("영천"),
    GYEONGSAN("경산"),
    CHEONGDO("청도"),
    CHILGOK("칠곡"),

    // 경상남도
    CHANGWON("창원"),
    GIMHAE("김해"),
    TONGYEONG("통영"),
    SACHEON("사천"),
    GEOJE("거제"),
    GOSEONG("고성"),
    NAMHAE("남해"),
    HAMYANG("함양"),
    GEOCHANG("거창"),
    HAPCHEON("합천"),
    MIRYANG("밀양"),
    UIRYEONG("의령"),
    HAMAN("함안"),
    CHANGNYEONG("창녕"),
    JINJU("진주"),
    SANCHEONG("산청"),
    HADONG("하동"),
    YANGSAN("양산"),

    // 제주특별자치도
    JEJU("제주"),
    SEOGWIPO("서귀포"),
    SEONGSAN("성산"),
    SEONGPANAK("성판악"),
    GOSAN("고산"),
    IEODO("이어도"),
    CHUJADO("추자도"),
    ;

    companion object {
        const val ALL_REGIONS_DESCRIPTION =
            "SEOUL(서울), BUSAN(부산), DAEGU(대구), INCHEON(인천), BAENGNYEONG(백령도), GANGHWA(강화), " +
                "GWANGJU(광주), DAEJEON(대전), ULSAN(울산), SEJONG(세종), " +
                "GWACHEON(과천), GWANGMYEONG(광명), GIMPO(김포), SIHEUNG(시흥), ANSAN(안산), BUCHEON(부천), " +
                "UIJEONGBU(의정부), GOYANG(고양), YANGJU(양주), PAJU(파주), DONGDUCHEON(동두천), " +
                "YEONCHEON(연천), POCHEON(포천), GAPYEONG(가평), GURI(구리), NAMYANGJU(남양주), " +
                "YANGPYEONG(양평), HANAM(하남), SUWON(수원), ANYANG(안양), OSAN(오산), HWASEONG(화성), " +
                "SEONGNAM(성남), PYEONGTAEK(평택), UIWANG(의왕), GUNPO(군포), ANSEONG(안성), YONGIN(용인), " +
                "ICHEON(이천), YEOJU(여주), CHEORWON(철원), HWACHEON(화천), INJE(인제), YANGGU(양구), " +
                "CHUNCHEON(춘천), HONGCHEON(홍천), WONJU(원주), HOENGSEONG(횡성), YEONGWOL(영월), " +
                "JEONGSEON(정선), PYEONGCHANG(평창), DAEGWALLYEONG(대관령), TAEBAEK(태백), SOKCHO(속초), " +
                "YANGYANG(양양), GANGNEUNG(강릉), DONGHAE(동해), SAMCHEOK(삼척), CHUNGJU(충주), " +
                "JINCHEON(진천), EUMSEONG(음성), JECHEON(제천), DANYANG(단양), CHEONGJU(청주), " +
                "BOEUN(보은), GOESAN(괴산), JEUNGPYEONG(증평), CHUPUNGNYEONG(추풍령), YEONGDONG(영동), " +
                "OKCHEON(옥천), SEOSAN(서산), TAEAN(태안), DANGJIN(당진), HONGSEONG(홍성), BORYEONG(보령), " +
                "SEOCHEON(서천), CHEONAN(천안), ASAN(아산), YESAN(예산), GONGJU(공주), GYERYONG(계룡), " +
                "BUYEO(부여), CHEONGYANG(청양), GEUMSAN(금산), NONSAN(논산), JEONJU(전주), IKSAN(익산), " +
                "JEONGEUP(정읍), WANJU(완주), JANGSU(장수), MUJU(무주), JINAN(진안), NAMWON(남원), " +
                "IMSIL(임실), SUNCHANG(순창), GUNSAN(군산), GIMJE(김제), GOCHANG(고창), BUAN(부안), " +
                "HAMPYEONG(함평), YEONGGWANG(영광), JINDO(진도), WANDO(완도), HAENAM(해남), GANGJIN(강진), " +
                "JANGHEUNG(장흥), YEOSU(여수), GWANGYANG(광양), GOHEUNG(고흥), BOSEONG(보성), " +
                "SUNCHEON(순천), JANGSEONG(장성), NAJU(나주), DAMYANG(담양), HWASUN(화순), GURYE(구례), " +
                "GOKSEONG(곡성), HEUKSANDO(흑산도), MOKPO(목포), YEONGAM(영암), SINAN(신안), MUAN(무안), " +
                "ULLEUNGDO(울릉도), DOKDO(독도), ULJIN(울진), YEONGDEOK(영덕), POHANG(포항), GYEONGJU(경주), " +
                "MUNGYEONG(문경), SANGJU(상주), YECHEON(예천), YEONGJU(영주), BONGHWA(봉화), " +
                "YEONGYANG(영양), ANDONG(안동), UISEONG(의성), CHEONGSONG(청송), GIMCHEON(김천), " +
                "GUMI(구미), GUNWI(군위), GORYEONG(고령), SEONGJU(성주), YEONGCHEON(영천), GYEONGSAN(경산), " +
                "CHEONGDO(청도), CHILGOK(칠곡), CHANGWON(창원), GIMHAE(김해), TONGYEONG(통영), " +
                "SACHEON(사천), GEOJE(거제), GOSEONG(고성), NAMHAE(남해), HAMYANG(함양), GEOCHANG(거창), " +
                "HAPCHEON(합천), MIRYANG(밀양), UIRYEONG(의령), HAMAN(함안), CHANGNYEONG(창녕), " +
                "JINJU(진주), SANCHEONG(산청), HADONG(하동), YANGSAN(양산), JEJU(제주), SEOGWIPO(서귀포), " +
                "SEONGSAN(성산), SEONGPANAK(성판악), GOSAN(고산), IEODO(이어도), CHUJADO(추자도)"
    }
}
