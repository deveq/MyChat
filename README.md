# MyChat
마이챗. 카카오톡 모방 어플. 친구추가를 통한 1:1 채팅

#### 개인프로젝트, Kotlin, Android Studio
#### 개발기간 : 2020.11.30 ~ 

#### MyChat을 만든 이유
바로 직전에 만들었던 Study Together라는 앱 내에 스터디 멤버들과 단체채팅을 하는 기능이 있었습니다.<br>
단순히 채팅에 가입된 멤버들끼리만 할 수 있는 단체채팅이었기 때문에 기능을 좀 더 추가하여 채팅앱을 만들어보고자하였고<br>
카톡에서 구현되어있는 채팅기능들을 구현함으로써 Firebase의 다양한 기능 및 Push Notification을 활용해보았습니다.<br>

## 애플리케이션 설명
ID를 통해 친구를 추가하고, 추가한 친구와 1:1대화를 할 수 있습니다.<br>
상대방이 읽었는지 여부를 확인할 수 있고 Push Notification을 활용하여 채팅을 보냈을 경우 상대방에게 Push 알림이 갑니다.<br>


#### 사용 라이브러리
1. Firebase 
    - Firestore : 회원정보 (User와 검색에 사용되는 Id) 
    - RealtimeDatabase : 채팅정보(Chatroom)
    - Storage : 사용자 프로필 이미지
    - Authentication : 이메일로 가입
    - Cloud Messaging : FCM을 이용한 Push

2. Retrofit
    - 스프링부트로 구현된 App Server에 push 요청을 하기 위해 사용
3. Glide
    - 사용자의 프로필 이미지 표시
4. Dexter
    - 권한 요청

## 순서
- 0. 완성 화면
- 1. 사용한 개념 및 기능
- 2. 느낀점

# MyChat
채팅앱 (진행중)
- 완료 목록
1. 아이디로 친구 찾기 
2. 채팅 목록에 가장 마지막 대화가 보이도록 하기
3. 읽음 여부 확인기능
4. 친구목록에서 현재 이름으로 검색
5. 날짜가 변하면 채팅방에도 날짜 표시
6. 채팅 보내면 상대방에게 push 알림
7. 프사올리기
친구추가


채팅방
![chatNreadCheck](https://user-images.githubusercontent.com/66777885/101208859-248b7880-36b6-11eb-83ff-1dca91277dc7.gif)

프로필 수정
![editProfile](https://user-images.githubusercontent.com/66777885/101208893-310fd100-36b6-11eb-9b44-418dd998837e.gif)

가입 및 id 설정


### 완성화면
#### 가입 및 로그인
![joinNsetId](https://user-images.githubusercontent.com/66777885/101208924-3b31cf80-36b6-11eb-8d09-a228d4863cb6.gif)

Firebase Authentication의 email로그인(회원가입)을 한 후<br>
만약 친구추가를 위해 사용할 ID가 지정되어있지 않다면 ID를 설정하는 다이얼로그가 show됩니다.<br>

#### 친구 추가
![addFriend](https://user-images.githubusercontent.com/66777885/101208813-1178a880-36b6-11eb-9fe5-4311fa0cb29b.gif)

친구목록 창 상단 Toolbar의 



가입하고자 하는 스터디 선택 후 가입신청을 하게되면 '가입대기중'상태가 되고,<br>
스터디의 리더가 가입승인을 하게 되면 멤버로써 가입이 완료됩니다.<br>
가입이 완료된 멤버라면 '가입'버튼이 '탈퇴'버튼으로 변경됩니다.<br>


2. 스터디 가입 승인하기<br>
![2_2 createStudyNImage](https://user-images.githubusercontent.com/66777885/101209113-9237a480-36b6-11eb-876c-2154303e2646.gif)

스터디의 리더가 가입신청목록에서 승인을 통해 가입신청한 사람을 승인할 수 있습니다.<br>
승인이 된다면 Firebase Firestore에서 Transaction을 통해 가입신청중 -> 가입완료 상태로 변경하고,<br>
해당 유저의 가입 스터디 목록에도 해당 스터디의 studyId를 추가합니다.<br>


3. 스터디 생성, 이미지 추가<br>
![2_2 createStudyNImage](https://user-images.githubusercontent.com/66777885/101209130-9e236680-36b6-11eb-9037-bf0ca72d416e.gif)

스터디를 생성하고자 하는 위치를 LongClick 시 생성을 위한 다이얼로그가 나타납니다.<br>
이름 옆 이미지 부분을 클릭하면 이미지의 Uri를 얻기위해 ACTION_APPLICATION_DETAILS_SETTINGS의 action 키워드를 가진 intent를 실행하게 되고<br>
사진 선택 후 해당 URI를 통해 Firebase Storage에 스터디의 이미지를 업로드할 수 있습니다. <br>

4. 스터디의 채팅방(대화방)<br>
![2_4 chat](https://user-images.githubusercontent.com/66777885/101209145-a7143800-36b6-11eb-9bd7-b71b7964f3a4.gif)

스터디에 가입한 후 멤버들과 채팅을 이용할 수 있습니다.<br>
Firebase RealtimeDatabase를 이용하였고, RecyclerView에 2개의 ViewHolder(ChatViewHolder와 DateViewHolder)를 이용하여<br>
혹시나 날짜가 변경되면 DateViewHolder의 view가 추가되어 출력되게끔, 날짜변경이 없다면 채팅이 출력되게 하였고,<br>
ChatViewAdapter에서는 채팅과 현재 사용자의 uid가 같은지 여부 확인 후,<br>
같다면 채팅view를 파란background + 오른쪽 정렬<br>
다르다면 회색background + 왼쪽정렬 + 이름 나타내기를 해주어 채팅을 구분할 수 있게 하였습니다.<br>

    
#### 동 이름 검색을 통해 스터디 찾기
동 이름 혹은 구 이름을 통해 검색한 뒤 해당 지역에 있는 스터디를 목록으로 보기.<br>
![3  searchList](https://user-images.githubusercontent.com/66777885/101209159-abd8ec00-36b6-11eb-807e-659a24e9d8f5.gif)

Kakao Local API를 이용하여 처음 화면에는 현재 위치의 위도, 경도(LatLng)를 통해 행정동 주소를 받고 그 주변의 스터디 리스트를 나타냅니다.<br>
그 후 검색을 통해 해당 지역의 스터디를 찾습니다.<br>
혹시 검색어를 통해 조회되는 지역이 2개 이상이라면 다이얼로그를 통해 선택 후 해당 지역의 스터디 목록을 받아옵니다.<br>


#### 개인정보
이름, 자기소개 수정 및 프로필 사진 추가<br>
![4  userInfo](https://user-images.githubusercontent.com/66777885/101209180-b4312700-36b6-11eb-9ba6-61082b252f09.gif)

이름, 자기소개 부분 클릭 시 다이얼로그를 통해 이름과 자기소개를 수정할 수 있습니다.<br>
프로필 사진 클릭 시 Firebase Storage에 이미지를 업로드 할 수 있습니다.<br>
이름,자기소개,프로필사진은 스터디페이지에 가입된 멤버들의 리스트를 통해서도 볼 수 있습니다.<br>


### 기능 설명
1. 채팅방
<pre><code>
    // DetailChatAdapter.kt
    
    
    override fun getItemViewType(position: Int): Int {
        // Chat객체의 type에 Chat.CHAT_TYPE 혹은 Chat.DATE_TYPE 2가지 값이 들어갈 수 있고
        // 해당 TYPE을 반환해주는 getItemViewType메서드를 override합니다.
        return chatList[position].type
    }
    
    // getItemViewType에서 반환된 viewType을 이용해서 ChatList에 있는 값이 채팅인지 날짜인지 구분합니다
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View
        val inflater = LayoutInflater.from(parent.context)

        // viewType에 따라서 반환해주는 ViewHolder를 다르게 해줍니다.
        return when (viewType) {
            Chat.CHAT_TYPE -> {
                view = inflater.inflate(R.layout.detail_chat_list_item, parent, false)
                DetailChatViewHolder(view)
            }
            Chat.DATE_TYPE -> {
                view = inflater.inflate(R.layout.detail_chat_date_list_item, parent, false)
                DetailChatDateViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }
</code></pre>

2. NaverMap API
MainMapFragment가 OnMapReadyCallback 인터페이스를 구현할 수 있도록한 후 onMapReady 메서드를 오버라이드합니다.
mapView가 로딩이 완료된 이후 getMapAsync를 통해 naverMap객체를 사용할 수 있는 onMapReady가 호출될 수 있게 한 후
LongClick에 대한 이벤트처리, Firebase Firestore에 저장된 Marker에 대한 정보(Point객체)를 받아 Marker로 표시할 수 있게 합니다.

3. Kakao Local API
REST API를 이용하여 GET방식으로 동이름 <-> 위,경도 정보를 받을 수 있는 API입니다.
(Naver Geolocation을 사용하려 하였으나 무료건수가 1000건이었기 때문에 무료인 Kakao Local API를 사용하였습니다.)
Retrofit을 이용하여 통신하였고, repsonse로 받는 정보 중 시(혹은 도) 이름인 depth1과 구 이름인 depth2를 이용해 주소를 받아왔습니다.
해당 API는 스터디 생성시, 리스트검색시 사용되었습니다.
<pre><code>
interface RetrofitService{

    //동 이름으로 DocAddr객체를 받아옵니다.
    //리스트로 검색 시 동 이름만 검색하여 주소를 받아온 후
    //그 주소와 동일한 Marker에 대한 정보를 Firestore를 통해 whereIn문으로 얻어올 수 있습니다.
    @GET("/v2/local/search/address.json")
    fun getByAdd(
        @Header("Authorization") appKey: String,
        @Query("query") address: String
    ) : Call<DocAddr>

    //위도,경도 정보를 통해 JsonObject를 콜백으로 받습니다.
    //리스트검색의 첫 화면에는 사용자의 위치정보를 통해 해당 지역의 정보를 얻은 다음
    //그 지역 내에 존재하는 스터디의 목록을 나타낼 수 있도록 하였습니다.
    //또 스터디 생성시 표시되는 지역명 또한 아래의 메서드를 통해 얻습니다.
    @GET("/v2/local/geo/coord2regioncode.json")
    fun getByGeo(
        @Header("Authorization") appKey: String,
        @Query("x") x: Double,
        @Query("y") y: Double
    ) : Call<JsonObject>
}
</code></pre>

### 마치며
작은 규모의 프로젝트만 만들다가 처음으로 1달이라는 시간이 소요되는 프로젝트를 만들어보았습니다.<br>
기존에는 책,인강 등 제공되는 틀에 제가 원하는 기능을 추가하여 만들었지만<br>
이번에는 Kakao Oven을 통해 프로토타입을 간단히 만들어보고 원하는 기능이 있다면 그것에 대한 자료를 구글링하여 만들어보고<br>
처음부터 끝까지 모든 코드에 노력을 담아서 만들었습니다.<br>
문제에 직면했을 때 해결되지 않는 부분은 1주일가까이 붙잡혀있었던적도 있었지만<br>
그러한 시행착오가 있었기 때문에 다양한 라이브러리를 시도해보고 Firebase와 REST API를 사용하는 방법에 대해 많이 알게되었습니다.<br>
다만 아쉬운점은 평일과 주말 가리지 않고 매일매일 12시간 이상씩 이 프로젝트 개발만 진행하였고<br>
1달이라는 시간이 지나 완성되었지만<br>
개인프로젝트이다보니.. 완성되었다는 기쁨을 함께 나눌 사람이 없어 많이 아쉬웠습니다...<br>
하지만 개인프로젝트이기 때문에 처음부터 끝까지 모든 부분을 제가 다 작성할 수 있어서<br>
다양한 기능들을 모두 접해볼 수 있는 아주 좋은 기회가 되었다고 생각합니다.<br>
다음에 어떤 프로젝트를 진행할지 아직 정하지는 않았지만<br>
이번 프로젝트를 통해 아주 많이 성장할 수 있었던 기회가 되었다고 생각합니다.<br>
<br>
읽어주셔서 감사합니다.<br>


<br>
...<br>
..<br>
.<br>
얼른 코로나 끝나고 마스크 벗고 한강에 따릉이 타면서 놀고싶습니다..ㅠㅠ<br>
다들 건강하시고.. 코로나를 이겨냅시다...<br>






푸시 알림을 구현하기 위해서는 앱서버가 꼭 필요하다는 사실을 몰라서 약 2주간 헤맸습니다..
