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






### 완성화면
#### 가입 및 로그인
![joinNsetId](https://user-images.githubusercontent.com/66777885/101208924-3b31cf80-36b6-11eb-8d09-a228d4863cb6.gif)

Firebase Authentication의 email로그인(회원가입)을 한 후<br>
만약 친구추가를 위해 사용할 ID가 지정되어있지 않다면 ID를 설정하는 다이얼로그가 show됩니다.<br>

#### 프로필 수정
![editProfile](https://user-images.githubusercontent.com/66777885/101208893-310fd100-36b6-11eb-9b44-418dd998837e.gif)<br>

친구목록 Fragment에서 자신의 프로필을 누르면 EditProfileActivity로 이동하게됩니다.<br>
이동한 후 수정을 완료하였을 경우 수정완료버튼을 통해<br>
이미지는 Storage에, User객체는 Image의 Uri를 받은 후 Firestore에 저장됩니다.


#### 친구 추가 및 찾기
1. 친구추가<br>
![addFriend2](https://user-images.githubusercontent.com/66777885/101213472-c6629380-36bd-11eb-9c01-61c4cdef1ab9.gif)<br>

친구목록 창 상단 Toolbar의 +버튼을 통해 친구추가할 수 있습니다.<br>
친구추가는 3가지 경우로 나뉩니다.<br>
ㄱ. 새로운 친구를 검색한 경우<br>
ㄴ. 이미 존재하는 친구를 검색한 경우<br>
ㄷ. 자신을 검색한 경우<br>
 ㄱ,ㄴ,ㄷ 모두 다른 View를 보여줍니다.<br>

2. 목록에서 이름 검색<br>
![searchFriends](https://user-images.githubusercontent.com/66777885/101213632-00cc3080-36be-11eb-9dd9-2864dea4036f.gif)<br>

추가되어있는 친구 목록에서 친구의 이름을 통해 찾을 수 있습니다.<br>
SearchView에 OnQueryTextListener를 추가하고<br>
onQueryTextChange를 override하여 입력된 문자가 변경될때마다 검색결과를 보여줍니다.<br>

#### 채팅
![chatNreadCheck](https://user-images.githubusercontent.com/66777885/101208859-248b7880-36b6-11eb-83ff-1dca91277dc7.gif) <br>

1. 3가지 ViewHolder
이전 프로젝트와는 달리 RecyclerView의 ViewHolder를 3가지로 나눴습니다<br>
ㄱ. 상대방 채팅의 ViewHolder<br>
ㄴ. 내 채팅의 ViewHolder<br>
ㄷ. 날짜 ViewHolder<br>
날짜의 경우 temp를 이용해 Comment객체의 timestamp를 날짜로 표현하는 방식을 이용했습니다.<br>

2. 읽음 표시<br>
RealtimeDatabase를 이용해 Chatroom객체의 reference를 얻은 뒤, Comment객체를 얻어옵니다.<br>
만약 읽은 상태라면 readUser에 자신의 uid와 함께 true값을 넣어줍니다.<br>
현재는 1:1채팅만 구현해놓은 상태이므로, readUser.size != 2 라면 아직 상대방이 읽지 않은 상태이므로<br>
나의 말풍선에 숫자 1을 나타내고<br>
만약 readUser.size == 2 라면 상대방이 읽었다는것을 의미하므로<br>
나의 말풍선에 숫자 1이 사라지게 됩니다.<br>

3. 채팅방 가장 마지막 채팅<br>
<pre><code>
    val treeMap = TreeMap<String, Comment>(Collection.reverseOrder())
    treeMap.put("comment의 key값", comment)
</code></pre>
RealtimeDatabase에 Comment값이 들어갈때는 push()메서드를 이용하므로 key값이 시간에 따라 자동지정됩니다.
그러므로 TreeMap의 key값에 참조의 key값의(commentRef.push().key()) 내림차순으로 정렬하면 가장 최근의 Comment값을 얻을 수 있으므로에
채팅목록에 표시될 가장 마지막 채팅과 시간을 나타낼 수 있습니다.
Firestore의 경우 FieldValue.serverTimestamp()를 통해 서버시간을 넣어줄 수 있었지만,
RealtimeDatabase의 경우 ServerValue.TIMESTAMP를 통해 서버시간을 얻었지만 타입이 Map<String, String>이었기 때문에 Comment객체에 들어가는 timestamp의 타입은 Any여야합니다.
(넣어줄때는 Map으로 넣었다 하더라도 서버에는 Long으로 저장되고 받을때도 Long으로 받아지므로)


#### 푸시 알림
![pushNotification](https://user-images.githubusercontent.com/66777885/101213594-f14ce780-36bd-11eb-993d-9cf513dc1538.gif)<br>

Firebase Cloud Messaging을 이용해 푸시 알림을 구현하였습니다.<br>
로그인이 완료된 이후 token을 얻어 Firestore의 User객체에 token값을 추가합니다.<br>
상대방에게 채팅을 보내면 token과 보낸사람이름(title), 내용(content)가 Retrofit을 이용해 POST Method로 요청되고<br>
서버에서는 받은 데이터들을 FCM서버에 요청하는 방식으로 진행됩니다.<br>

### 마치며
MyChat은 바로 직전에 완성된 Study Together보다 조금 더 일찍 구상한 프로젝트이지만<br>
도중에 잠시 중단하였다가 Study Together가 완성된 이후 다시 진행하게 되었습니다.<br>
이번 프로젝트는 새로운 라이브러리나 API를 이용하기보다<br>
기존에 얕게 알고 있었던 기능들에 대해 좀 더 활용할 수 있는 방향으로 진행하였습니다.<br>
그 덕분에 코드를 쓰면서도 이해가 잘 되지 않았던 부분들을 배울 수 있는 좋은 기회가 되었습니다.<br>
특히 Fragment와 Activity간의 생명주기에 대해 조금 더 익힐 수 있었고<br>
ViewModel의 Onwer에 대한 개념을 좀 더 깊히 이해할 수 있게 되었습니다.



그 덕분에 공부 초반에는 어려웠던 개념들에 대해 더 깊히 이해할 수 있는 시간이 되었습니다.<br>


푸시 알림을 구현하기 위해서는 앱서버가 꼭 필요하다는 사실을 몰라서 며칠동안 힘들었지만 뒤늦게 자료를 찾아 그 방식대로 진행하였고

