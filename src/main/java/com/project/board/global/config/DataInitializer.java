package com.project.board.global.config;

import com.project.board.domain.bookmark.entity.Bookmark;
import com.project.board.domain.bookmark.repository.BookmarkRepository;
import com.project.board.domain.category.entity.Category;
import com.project.board.domain.category.repository.CategoryRepository;
import com.project.board.domain.comment.entity.Comment;
import com.project.board.domain.comment.repository.CommentRepository;
import com.project.board.domain.like.entity.CommentLike;
import com.project.board.domain.like.entity.PostLike;
import com.project.board.domain.like.repository.CommentLikeRepository;
import com.project.board.domain.like.repository.PostLikeRepository;
import com.project.board.domain.notification.entity.Notification;
import com.project.board.domain.notification.repository.NotificationRepository;
import com.project.board.domain.post.entity.Post;
import com.project.board.domain.post.repository.PostRepository;
import com.project.board.domain.report.entity.Report;
import com.project.board.domain.report.repository.ReportRepository;
import com.project.board.domain.user.entity.User;
import com.project.board.domain.user.repository.UserRepository;
import com.project.board.domain.viewlog.entity.ViewLog;
import com.project.board.domain.viewlog.repository.ViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NotificationRepository notificationRepository;
    private final ReportRepository reportRepository;
    private final ViewLogRepository viewLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        // ============================================================
        // 카테고리
        // ============================================================
        Category notice = categoryRepository.save(new Category("공지"));
        Category free = categoryRepository.save(new Category("자유"));
        Category question = categoryRepository.save(new Category("질문"));
        Category info = categoryRepository.save(new Category("정보공유"));

        // ============================================================
        // 유저 (6명)
        // ============================================================
        User admin = userRepository.save(User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin1234"))
                .nickname("관리자")
                .role(User.Role.ADMIN)
                .build());

        User user1 = userRepository.save(User.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("개발초보")
                .build());

        User user2 = userRepository.save(User.builder()
                .email("test2@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("코딩마스터")
                .build());

        User user3 = userRepository.save(User.builder()
                .email("test3@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("자바킹")
                .build());

        User user4 = userRepository.save(User.builder()
                .email("test4@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("리액트러버")
                .build());

        User user5 = userRepository.save(User.builder()
                .email("test5@test.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("취준생김철수")
                .build());

        // ============================================================
        // 공지글
        // ============================================================
        Post noticePost1 = postRepository.save(Post.builder()
                .user(admin).category(notice)
                .title("[필독] 게시판 이용규칙 안내")
                .content("안녕하세요, 관리자입니다.\n\n1. 욕설, 비방 금지\n2. 광고성 게시글 금지\n3. 중복 게시글 금지\n4. 개인정보 노출 금지\n\n위반 시 경고 없이 게시글이 삭제될 수 있습니다.\n감사합니다.")
                .build());

        Post noticePost2 = postRepository.save(Post.builder()
                .user(admin).category(notice)
                .title("[공지] 서버 점검 안내 (2/25 02:00~06:00)")
                .content("서버 점검이 예정되어 있습니다.\n\n일시: 2025년 2월 25일 새벽 2시 ~ 6시\n내용: DB 마이그레이션 및 성능 개선\n\n해당 시간 동안 서비스 이용이 불가합니다.\n양해 부탁드립니다.")
                .build());

        // ============================================================
        // 자유 게시판
        // ============================================================
        Post p1 = postRepository.save(Post.builder()
                .user(user1).category(free)
                .title("개발 시작한 지 한 달 됐습니다")
                .content("자바 배우기 시작했는데 진짜 어렵네요...\n변수 타입부터 헷갈리고 클래스가 뭔지도 모르겠고\n그래도 포기 안 하고 해보겠습니다!\n응원해주세요 ㅠㅠ")
                .build());

        Post p2 = postRepository.save(Post.builder()
                .user(user2).category(free)
                .title("오늘 코딩테스트 봤는데 망했습니다")
                .content("카카오 코테 봤는데 2번까지밖에 못 풀었어요\n3번부터 그래프 문제 나오는데 손도 못 댔습니다\n다들 코테 어떻게 준비하시나요?")
                .build());

        Post p3 = postRepository.save(Post.builder()
                .user(user3).category(free)
                .title("자바 vs 파이썬 뭐가 더 좋음?")
                .content("저는 자바가 좋은데 주변에서 파이썬 파이썬 하길래...\n취업할 때 어떤 게 더 유리할까요?\n솔직히 자바가 취업은 더 잘 되지 않나요?")
                .build());

        Post p4 = postRepository.save(Post.builder()
                .user(user4).category(free)
                .title("리액트 진짜 재밌다")
                .content("처음에 useState 이해 안 됐는데 한번 이해하니까 완전 재밌어요!\n컴포넌트 나누는 것도 레고 조립하는 느낌이고\n다들 리액트 해보세요 강추합니다")
                .build());

        Post p5 = postRepository.save(Post.builder()
                .user(user5).category(free)
                .title("취업 준비하다가 현타 옵니다")
                .content("포트폴리오 만들고 이력서 쓰고 코테 준비하고...\n6개월째 하고 있는데 아직 합격 소식이 없네요\n다들 어떻게 버티시나요? 진짜 힘듭니다")
                .build());

        Post p6 = postRepository.save(Post.builder()
                .user(user1).category(free)
                .title("오늘 드디어 첫 프로젝트 완성!!")
                .content("투두리스트 앱인데 완성했습니다!!\nCRUD 전부 구현했고 CSS도 나름 꾸몄어요\n별거 아닌 거지만 뿌듯하네요 ㅎㅎ")
                .build());

        Post p7 = postRepository.save(Post.builder()
                .user(user2).category(free)
                .title("야근 5일째 하는 중입니다")
                .content("SI 프로젝트 마감이 다음 주인데 버그가 계속 나와요\n고객사에서 요구사항도 계속 바꾸고...\n이게 개발자 현실인가요?")
                .build());

        // ============================================================
        // 질문 게시판
        // ============================================================
        Post p8 = postRepository.save(Post.builder()
                .user(user1).category(question)
                .title("Spring Boot에서 JPA N+1 문제 어떻게 해결하나요?")
                .content("게시글 목록 조회할 때 쿼리가 엄청 많이 나가요\n@OneToMany 관계에서 발생하는 것 같은데\nfetch join이라는 걸 써야 한다고 하는데 정확히 어떻게 하는 건가요?")
                .build());

        Post p9 = postRepository.save(Post.builder()
                .user(user4).category(question)
                .title("React useEffect 무한루프 도와주세요")
                .content("useEffect 안에서 setState 하면 계속 무한으로 렌더링돼요\n의존성 배열에 state를 넣으면 무한루프가 되고\n안 넣으면 경고가 뜨고... 어떻게 해야 하나요?")
                .build());

        Post p10 = postRepository.save(Post.builder()
                .user(user5).category(question)
                .title("Git merge conflict 해결하는 법 알려주세요")
                .content("팀 프로젝트 하는데 merge할 때마다 충돌이 납니다\n같은 파일 수정하면 항상 이러는 건가요?\n해결하는 좋은 방법이 있을까요?")
                .build());

        Post p11 = postRepository.save(Post.builder()
                .user(user3).category(question)
                .title("스프링 시큐리티 JWT 구현 질문")
                .content("JWT 토큰 인증 구현하고 있는데요\naccessToken이 만료됐을 때 refreshToken으로 재발급하는 흐름이 이해가 안 돼요\n프론트에서 401 받으면 자동으로 재발급 요청하는 건가요?")
                .build());

        Post p12 = postRepository.save(Post.builder()
                .user(user1).category(question)
                .title("CSS flexbox vs grid 언제 뭘 쓰나요?")
                .content("레이아웃 잡을 때 flexbox랑 grid 중에 뭘 써야 할지 모르겠어요\n둘 다 배웠는데 실전에서 어떤 기준으로 선택하시나요?")
                .build());

        // ============================================================
        // 정보공유 게시판
        // ============================================================
        Post p13 = postRepository.save(Post.builder()
                .user(user2).category(info)
                .title("[정리] 주니어 개발자 면접 단골 질문 TOP 10")
                .content("면접 다니면서 정리한 질문들입니다.\n\n1. OOP 4대 특성\n2. HTTP vs HTTPS 차이\n3. REST API란?\n4. 데이터베이스 인덱스\n5. 트랜잭션 ACID\n6. 자바 GC 동작원리\n7. 스프링 IoC/DI\n8. TCP vs UDP\n9. 프로세스 vs 스레드\n10. 정규화\n\n각 항목 상세 설명은 추후 올리겠습니다!")
                .build());

        Post p14 = postRepository.save(Post.builder()
                .user(user3).category(info)
                .title("무료 개발 강의 사이트 모음")
                .content("제가 공부하면서 유용했던 사이트들 공유합니다.\n\n- 인프런 무료 강의\n- 유튜브 코딩애플\n- 노마드코더\n- 생활코딩\n- MDN Web Docs\n- Baeldung (스프링)\n\n다들 화이팅!")
                .build());

        Post p15 = postRepository.save(Post.builder()
                .user(user4).category(info)
                .title("React 프로젝트 폴더 구조 추천")
                .content("여러 프로젝트 해보면서 정착한 구조입니다.\n\nsrc/\n  api/       - API 호출 함수\n  components/ - 재사용 컴포넌트\n  contexts/  - Context API\n  pages/     - 페이지 컴포넌트\n  styles/    - CSS 파일\n  utils/     - 유틸 함수\n\n참고하세요!")
                .build());

        Post p16 = postRepository.save(Post.builder()
                .user(user5).category(info)
                .title("이력서 쓸 때 이것만은 하지 마세요")
                .content("현직 개발자 멘토님한테 들은 조언입니다.\n\n1. '성실합니다' 같은 추상적 표현 금지\n2. 프로젝트 나열만 하지 말고 본인 역할 구체적으로\n3. 기술 스택 나열하지 말고 왜 선택했는지\n4. 깃헙 링크는 필수\n5. README 꼭 작성할 것\n\n도움이 됐으면 좋겠습니다!")
                .build());

        Post p17 = postRepository.save(Post.builder()
                .user(user2).category(info)
                .title("Docker 입문자를 위한 핵심 명령어 정리")
                .content("도커 처음 배울 때 필요한 명령어만 모았습니다.\n\ndocker build -t 이미지명 .\ndocker run -p 8080:8080 이미지명\ndocker ps\ndocker stop 컨테이너ID\ndocker-compose up -d\n\n이것만 알아도 기본은 됩니다!")
                .build());

        Post p18 = postRepository.save(Post.builder()
                .user(user3).category(free)
                .title("맥북 vs 윈도우 개발환경 논쟁 종결")
                .content("결론: 둘 다 좋음. 본인한테 익숙한 거 쓰세요.\n맥이 개발하기 편한 건 맞는데 윈도우도 WSL2 나온 이후로 충분합니다.\n돈 없으면 윈도우, 있으면 맥 사세요. 끝.")
                .build());

        Post p19 = postRepository.save(Post.builder()
                .user(user4).category(free)
                .title("다들 개발할 때 음악 들으시나요?")
                .content("저는 로파이 틀어놓고 코딩하는데\n다들 뭐 들으면서 개발하시나요?\n추천 플레이리스트 있으면 공유해주세요!")
                .build());

        Post p20 = postRepository.save(Post.builder()
                .user(user5).category(question)
                .title("포트폴리오 프로젝트 주제 추천해주세요")
                .content("게시판은 너무 흔하다고 해서 다른 주제를 찾고 있어요\n쇼핑몰? 채팅앱? 뭐가 좋을까요?\n면접관이 관심 가질 만한 주제 추천 부탁드립니다!")
                .build());

        // ============================================================
        // 댓글 (멘션 포함, 활발한 토론)
        // ============================================================

        // p3: 자바 vs 파이썬 논쟁
        Comment c1 = commentRepository.save(Comment.builder()
                .post(p3).user(user4)
                .content("파이썬이 훨씬 낫죠. 코드도 깔끔하고 배우기도 쉽고. 자바는 너무 장황함")
                .build());
        Comment c2 = commentRepository.save(Comment.builder()
                .post(p3).user(user3)
                .content("자바가 장황하긴 한데 그만큼 명확하잖아요. 대규모 프로젝트에서는 자바가 훨씬 관리하기 편함")
                .build());
        commentRepository.save(Comment.builder()
                .post(p3).user(user4).parent(c2)
                .content("@자바킹 대규모 프로젝트? 인스타그램 파이썬으로 만들었는데요? ㅋㅋ")
                .build());
        commentRepository.save(Comment.builder()
                .post(p3).user(user2).parent(c2)
                .content("@리액트러버 @자바킹 둘 다 배우면 됩니다 싸우지 마세요 ㅋㅋㅋ")
                .build());
        commentRepository.save(Comment.builder()
                .post(p3).user(user5)
                .content("취업만 생각하면 자바가 압도적으로 공고 많습니다. 현실적으로 봐야죠")
                .build());
        commentRepository.save(Comment.builder()
                .post(p3).user(user1).parent(c1)
                .content("파이썬 쉽다고 하는데 저는 들여쓰기 때문에 더 헷갈리던데...")
                .build());
        p3.increaseCommentCount(); p3.increaseCommentCount(); p3.increaseCommentCount();
        p3.increaseCommentCount(); p3.increaseCommentCount(); p3.increaseCommentCount();

        // p2: 코딩테스트 망한 글
        Comment c3 = commentRepository.save(Comment.builder()
                .post(p2).user(user3)
                .content("2번까지 풀었으면 그래도 괜찮은 거 아닌가요? 저는 1번도 못 풀었는데 ㅋㅋ")
                .build());
        commentRepository.save(Comment.builder()
                .post(p2).user(user5)
                .content("백준 골드 수준까지 풀면 카카오 3번까지는 풀 수 있어요. 꾸준히 하세요!")
                .build());
        commentRepository.save(Comment.builder()
                .post(p2).user(user1).parent(c3)
                .content("@자바킹 저도 1번 겨우 풀었어요... 코테는 진짜 따로 시간 잡고 준비해야 하는 것 같아요")
                .build());
        p2.increaseCommentCount(); p2.increaseCommentCount(); p2.increaseCommentCount();

        // p5: 취업 준비 현타
        Comment c4 = commentRepository.save(Comment.builder()
                .post(p5).user(user2)
                .content("저도 8개월 걸렸어요. 포기하지 마세요. 반드시 됩니다!")
                .build());
        commentRepository.save(Comment.builder()
                .post(p5).user(user3)
                .content("프로젝트 퀄리티를 높이는 게 중요해요. 개수보다 깊이!")
                .build());
        commentRepository.save(Comment.builder()
                .post(p5).user(user4).parent(c4)
                .content("@코딩마스터 맞아요 저도 7개월 만에 됐어요. 꾸준히 하면 결과 나옵니다")
                .build());
        commentRepository.save(Comment.builder()
                .post(p5).user(user1)
                .content("같이 힘내봐요 ㅠㅠ 저도 취준 중입니다")
                .build());
        p5.increaseCommentCount(); p5.increaseCommentCount();
        p5.increaseCommentCount(); p5.increaseCommentCount();

        // p8: JPA N+1 질문
        Comment c5 = commentRepository.save(Comment.builder()
                .post(p8).user(user3)
                .content("fetch join 쓰세요. @Query에서 JOIN FETCH 하면 됩니다")
                .build());
        Comment c5r1 = commentRepository.save(Comment.builder()
                .post(p8).user(user2).parent(c5)
                .content("fetch join은 페이징이랑 같이 못 쓰는 거 아시죠? @BatchSize 도 같이 알아보세요")
                .build());
        commentRepository.save(Comment.builder()
                .post(p8).user(user1).parent(c5)
                .content("@자바킹 감사합니다! fetch join으로 해결됐어요!")
                .build());
        p8.increaseCommentCount(); p8.increaseCommentCount(); p8.increaseCommentCount();

        // p9: React useEffect 무한루프
        Comment c9_1 = commentRepository.save(Comment.builder()
                .post(p9).user(user2)
                .content("의존성 배열에 객체 넣으면 매번 새 참조라서 무한루프 됩니다. useMemo로 감싸보세요")
                .build());
        Comment c9_2 = commentRepository.save(Comment.builder()
                .post(p9).user(user1)
                .content("저도 이거 때문에 3시간 날렸어요 ㅋㅋ useCallback 이랑 useMemo 공부하세요")
                .build());
        commentRepository.save(Comment.builder()
                .post(p9).user(user4).parent(c9_1)
                .content("@코딩마스터 오 감사합니다! useMemo로 감싸니까 해결됐어요!!")
                .build());
        p9.increaseCommentCount(); p9.increaseCommentCount(); p9.increaseCommentCount();

        // p6: 첫 프로젝트 완성
        Comment c6_1 = commentRepository.save(Comment.builder()
                .post(p6).user(user2)
                .content("축하합니다! 첫 프로젝트 완성이 가장 의미 있어요. 다음엔 API 연동도 해보세요!")
                .build());
        commentRepository.save(Comment.builder()
                .post(p6).user(user3)
                .content("투두리스트가 별거 아닌 것 같지만 CRUD 다 들어있어서 좋은 연습이에요")
                .build());
        commentRepository.save(Comment.builder()
                .post(p6).user(user5)
                .content("@개발초보 저도 처음에 투두리스트로 시작했어요! 화이팅!")
                .build());
        p6.increaseCommentCount(); p6.increaseCommentCount(); p6.increaseCommentCount();

        // p13: 면접 질문
        Comment c13_1 = commentRepository.save(Comment.builder()
                .post(p13).user(user1)
                .content("우와 정리 감사합니다! 저장해둘게요")
                .build());
        commentRepository.save(Comment.builder()
                .post(p13).user(user5)
                .content("여기에 CORS랑 쿠키/세션 차이도 추가하면 좋을 것 같아요!")
                .build());
        commentRepository.save(Comment.builder()
                .post(p13).user(user3)
                .content("@코딩마스터 상세 설명 빨리 올려주세요! 면접 준비 중입니다")
                .build());
        commentRepository.save(Comment.builder()
                .post(p13).user(user4).parent(c13_1)
                .content("@개발초보 저도 저장했어요 ㅋㅋ 같이 면접 준비 해요!")
                .build());
        p13.increaseCommentCount(); p13.increaseCommentCount();
        p13.increaseCommentCount(); p13.increaseCommentCount();

        // p18: 맥북 vs 윈도우
        Comment c6 = commentRepository.save(Comment.builder()
                .post(p18).user(user4)
                .content("맥 쓰면 다시는 윈도우 못 돌아감 ㅋㅋ 트랙패드 한번 써보세요")
                .build());
        Comment c7 = commentRepository.save(Comment.builder()
                .post(p18).user(user5)
                .content("윈도우 유저인데 솔직히 맥 안 써봐서 모르겠음. 근데 WSL2로 충분한데 굳이?")
                .build());
        commentRepository.save(Comment.builder()
                .post(p18).user(user4).parent(c7)
                .content("@취준생김철수 써보면 압니다. 개발 환경 세팅부터가 차원이 다름")
                .build());
        commentRepository.save(Comment.builder()
                .post(p18).user(user3).parent(c6)
                .content("@리액트러버 맥 300만원 주고 사서 유튜브만 보는 사람도 있던데 ㅋㅋ")
                .build());
        commentRepository.save(Comment.builder()
                .post(p18).user(user2)
                .content("글쓴이 말이 맞음. 그냥 본인한테 맞는 거 쓰면 됨. 싸울 주제가 아님")
                .build());
        p18.increaseCommentCount(); p18.increaseCommentCount(); p18.increaseCommentCount();
        p18.increaseCommentCount(); p18.increaseCommentCount();

        // p20: 포트폴리오 주제
        Comment c20_1 = commentRepository.save(Comment.builder()
                .post(p20).user(user2)
                .content("실시간 채팅앱 추천합니다. WebSocket 써봤다고 하면 면접에서 관심 가져요")
                .build());
        commentRepository.save(Comment.builder()
                .post(p20).user(user3)
                .content("게시판이 흔하다고 하지만 완성도 높으면 충분합니다. 중요한 건 깊이예요")
                .build());
        commentRepository.save(Comment.builder()
                .post(p20).user(user4)
                .content("중고거래 플랫폼은 어떠세요? 결제 연동까지 하면 포트폴리오로 괜찮을 듯")
                .build());
        commentRepository.save(Comment.builder()
                .post(p20).user(user1).parent(c20_1)
                .content("@코딩마스터 WebSocket이면 STOMP도 같이 배워야 하나요?")
                .build());
        p20.increaseCommentCount(); p20.increaseCommentCount();
        p20.increaseCommentCount(); p20.increaseCommentCount();

        // p19: 음악 추천
        commentRepository.save(Comment.builder()
                .post(p19).user(user1)
                .content("저는 그냥 조용한 게 좋아서 무음으로 합니다 ㅋㅋ")
                .build());
        commentRepository.save(Comment.builder()
                .post(p19).user(user3)
                .content("저는 게임 OST 틀어놓아요. 위쳐3 사운드트랙 추천합니다")
                .build());
        commentRepository.save(Comment.builder()
                .post(p19).user(user5)
                .content("코딩할 때 가사 있는 노래 들으면 집중 안 돼요. 로파이가 최고")
                .build());
        p19.increaseCommentCount(); p19.increaseCommentCount(); p19.increaseCommentCount();

        // p1: 개발 시작
        commentRepository.save(Comment.builder()
                .post(p1).user(user2)
                .content("한 달이면 아직 시작이에요! 3개월만 꾸준히 하면 확 달라집니다. 화이팅!")
                .build());
        commentRepository.save(Comment.builder()
                .post(p1).user(user4)
                .content("저도 처음엔 그랬어요 ㅋㅋ 지금은 리액트로 프로젝트 하고 있습니다. 포기만 안 하면 됩니다!")
                .build());
        p1.increaseCommentCount(); p1.increaseCommentCount();

        // p16: 이력서 조언
        Comment c16_1 = commentRepository.save(Comment.builder()
                .post(p16).user(user1)
                .content("README 진짜 중요하더라고요. 저도 최근에야 깨달았어요")
                .build());
        commentRepository.save(Comment.builder()
                .post(p16).user(user3)
                .content("@취준생김철수 좋은 정보 감사합니다! 이력서 다시 써야겠어요")
                .build());
        p16.increaseCommentCount(); p16.increaseCommentCount();

        postRepository.flush();

        // ============================================================
        // 조회수 (ViewLog + viewCount 직접 반영)
        // ============================================================
        // 인기 글들에 조회수 부여
        addView(user1, p13); addView(user2, p13); addView(user3, p13); addView(user4, p13); addView(user5, p13); // 면접질문 5
        addView(user1, p3); addView(user2, p3); addView(user4, p3); addView(user5, p3); // 자바vs파이썬 4
        addView(user1, p5); addView(user2, p5); addView(user3, p5); addView(user4, p5); // 취업현타 4
        addView(user1, p14); addView(user2, p14); addView(user4, p14); addView(user5, p14); // 무료강의 4
        addView(user1, p16); addView(user2, p16); addView(user3, p16); addView(user4, p16); // 이력서 4
        addView(user1, p18); addView(user2, p18); addView(user4, p18); // 맥vs윈도우 3
        addView(user1, p8); addView(user3, p8); addView(user4, p8); // JPA N+1 3
        addView(user2, p6); addView(user3, p6); addView(user5, p6); // 첫프로젝트 3
        addView(user1, p2); addView(user3, p2); addView(user5, p2); // 코테 3
        addView(user1, p20); addView(user2, p20); addView(user3, p20); // 포트폴리오 3
        addView(user1, p9); addView(user2, p9); // useEffect 2
        addView(user2, p17); addView(user5, p17); // Docker 2
        addView(user1, p15); addView(user3, p15); // React구조 2
        addView(user2, p4); addView(user5, p4); // 리액트재밌다 2
        addView(user3, p7); addView(user4, p7); // 야근 2
        addView(user1, p19); // 음악 1
        addView(user3, p11); addView(user4, p11); // JWT질문 2
        addView(user2, p12); addView(user4, p12); // CSS질문 2
        addView(user1, p10); addView(user3, p10); // Git질문 2

        // ============================================================
        // 게시글 좋아요 (PostLike + likeCount 반영)
        // ============================================================
        // 면접질문 - 정보글이라 좋아요 많음
        addPostLike(user1, p13); addPostLike(user3, p13); addPostLike(user4, p13); addPostLike(user5, p13);
        // 무료 강의 - 유용한 정보
        addPostLike(user1, p14); addPostLike(user2, p14); addPostLike(user5, p14);
        // 이력서 조언
        addPostLike(user1, p16); addPostLike(user2, p16); addPostLike(user3, p16);
        // 취업 현타 - 공감 많음
        addPostLike(user1, p5); addPostLike(user2, p5); addPostLike(user3, p5);
        // 첫 프로젝트 완성 - 응원
        addPostLike(user2, p6); addPostLike(user4, p6); addPostLike(user5, p6);
        // Docker 정리
        addPostLike(user1, p17); addPostLike(user4, p17);
        // React 폴더구조
        addPostLike(user1, p15); addPostLike(user3, p15);
        // 자바vs파이썬 - 토론글 호불호
        addPostLike(user4, p3); addPostLike(user5, p3);
        // 리액트 재밌다
        addPostLike(user1, p4); addPostLike(user2, p4);
        // 맥북 vs 윈도우
        addPostLike(user2, p18); addPostLike(user4, p18);
        // 야근 글 - 동감
        addPostLike(user3, p7); addPostLike(user5, p7);
        // JPA N+1
        addPostLike(user3, p8);
        // 코딩테스트
        addPostLike(user1, p2);
        // 음악
        addPostLike(user3, p19);

        // ============================================================
        // 댓글 좋아요 (CommentLike + likeCount 반영)
        // ============================================================
        // 도움 되는 답변들에 좋아요
        addCommentLike(user1, c5);  // fetch join 답변
        addCommentLike(user4, c5);
        addCommentLike(user1, c9_1); // useMemo 답변
        addCommentLike(user4, c9_1);
        addCommentLike(user1, c4);  // 취업 응원
        addCommentLike(user5, c4);
        addCommentLike(user2, c1);  // 파이썬 옹호
        addCommentLike(user1, c13_1); // 면접 정리 감사
        addCommentLike(user3, c6_1);  // 첫프로젝트 축하
        addCommentLike(user5, c6_1);
        addCommentLike(user3, c20_1); // 채팅앱 추천
        addCommentLike(user5, c20_1);
        addCommentLike(user2, c5r1);  // BatchSize 팁
        addCommentLike(user1, c16_1); // README 중요

        // ============================================================
        // 북마크 (자주 참고할 만한 글 저장)
        // ============================================================
        // user1(개발초보) - 공부자료 북마크
        bookmarkRepository.save(new Bookmark(user1, p13)); // 면접질문
        bookmarkRepository.save(new Bookmark(user1, p14)); // 무료강의
        bookmarkRepository.save(new Bookmark(user1, p16)); // 이력서
        bookmarkRepository.save(new Bookmark(user1, p15)); // React구조
        bookmarkRepository.save(new Bookmark(user1, p17)); // Docker

        // user2(코딩마스터) - 정보글 위주
        bookmarkRepository.save(new Bookmark(user2, p14)); // 무료강의
        bookmarkRepository.save(new Bookmark(user2, p16)); // 이력서

        // user3(자바킹) - 기술 관련
        bookmarkRepository.save(new Bookmark(user3, p13)); // 면접질문
        bookmarkRepository.save(new Bookmark(user3, p8));  // JPA N+1
        bookmarkRepository.save(new Bookmark(user3, p17)); // Docker

        // user4(리액트러버) - 프론트 관련
        bookmarkRepository.save(new Bookmark(user4, p15)); // React구조
        bookmarkRepository.save(new Bookmark(user4, p13)); // 면접질문

        // user5(취준생김철수) - 취업 관련
        bookmarkRepository.save(new Bookmark(user5, p13)); // 면접질문
        bookmarkRepository.save(new Bookmark(user5, p16)); // 이력서
        bookmarkRepository.save(new Bookmark(user5, p14)); // 무료강의
        bookmarkRepository.save(new Bookmark(user5, p5));  // 취업현타 공감

        // ============================================================
        // 알림 (다양한 타입)
        // ============================================================

        // COMMENT 알림 - 내 글에 댓글 달림
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.COMMENT)
                .postId(p3.getId()).actorId(user4.getId())
                .message("리액트러버님이 회원님의 글에 댓글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.COMMENT)
                .postId(p2.getId()).actorId(user3.getId())
                .message("자바킹님이 회원님의 글에 댓글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user5).type(Notification.NotificationType.COMMENT)
                .postId(p5.getId()).actorId(user2.getId())
                .message("코딩마스터님이 회원님의 글에 댓글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user1).type(Notification.NotificationType.COMMENT)
                .postId(p8.getId()).actorId(user3.getId())
                .message("자바킹님이 회원님의 글에 댓글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user1).type(Notification.NotificationType.COMMENT)
                .postId(p6.getId()).actorId(user2.getId())
                .message("코딩마스터님이 회원님의 글에 댓글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.COMMENT)
                .postId(p13.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 글에 댓글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user4).type(Notification.NotificationType.COMMENT)
                .postId(p9.getId()).actorId(user2.getId())
                .message("코딩마스터님이 회원님의 글에 댓글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user5).type(Notification.NotificationType.COMMENT)
                .postId(p16.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 글에 댓글을 달았습니다.")
                .build());

        // REPLY 알림 - 내 댓글에 대댓글 달림
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.REPLY)
                .postId(p3.getId()).actorId(user4.getId())
                .message("리액트러버님이 회원님의 댓글에 답글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user4).type(Notification.NotificationType.REPLY)
                .postId(p3.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 댓글에 답글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.REPLY)
                .postId(p8.getId()).actorId(user2.getId())
                .message("코딩마스터님이 회원님의 댓글에 답글을 달았습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.REPLY)
                .postId(p9.getId()).actorId(user4.getId())
                .message("리액트러버님이 회원님의 댓글에 답글을 달았습니다.")
                .build());

        // POST_LIKE 알림 - 내 글에 좋아요
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.POST_LIKE)
                .postId(p13.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 글을 좋아합니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user5).type(Notification.NotificationType.POST_LIKE)
                .postId(p5.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 글을 좋아합니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user1).type(Notification.NotificationType.POST_LIKE)
                .postId(p6.getId()).actorId(user2.getId())
                .message("코딩마스터님이 회원님의 글을 좋아합니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.POST_LIKE)
                .postId(p14.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 글을 좋아합니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user4).type(Notification.NotificationType.POST_LIKE)
                .postId(p4.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 글을 좋아합니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user5).type(Notification.NotificationType.POST_LIKE)
                .postId(p16.getId()).actorId(user2.getId())
                .message("코딩마스터님이 회원님의 글을 좋아합니다.")
                .build());

        // COMMENT_LIKE 알림 - 내 댓글에 좋아요
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.COMMENT_LIKE)
                .postId(p8.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 댓글을 좋아합니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.COMMENT_LIKE)
                .postId(p9.getId()).actorId(user4.getId())
                .message("리액트러버님이 회원님의 댓글을 좋아합니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.COMMENT_LIKE)
                .postId(p5.getId()).actorId(user1.getId())
                .message("개발초보님이 회원님의 댓글을 좋아합니다.")
                .build());

        // MENTION 알림 - 댓글에서 멘션
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.MENTION)
                .postId(p3.getId()).actorId(user4.getId())
                .message("리액트러버님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user4).type(Notification.NotificationType.MENTION)
                .postId(p3.getId()).actorId(user2.getId())
                .message("코딩마스터님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.MENTION)
                .postId(p3.getId()).actorId(user2.getId())
                .message("코딩마스터님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.MENTION)
                .postId(p5.getId()).actorId(user4.getId())
                .message("리액트러버님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.MENTION)
                .postId(p8.getId()).actorId(user1.getId())
                .message("개발초보님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user3).type(Notification.NotificationType.MENTION)
                .postId(p2.getId()).actorId(user1.getId())
                .message("개발초보님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.MENTION)
                .postId(p9.getId()).actorId(user4.getId())
                .message("리액트러버님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user1).type(Notification.NotificationType.MENTION)
                .postId(p6.getId()).actorId(user5.getId())
                .message("취준생김철수님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user5).type(Notification.NotificationType.MENTION)
                .postId(p16.getId()).actorId(user3.getId())
                .message("자바킹님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.MENTION)
                .postId(p20.getId()).actorId(user1.getId())
                .message("개발초보님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user5).type(Notification.NotificationType.MENTION)
                .postId(p18.getId()).actorId(user4.getId())
                .message("리액트러버님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user4).type(Notification.NotificationType.MENTION)
                .postId(p18.getId()).actorId(user3.getId())
                .message("자바킹님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user1).type(Notification.NotificationType.MENTION)
                .postId(p13.getId()).actorId(user4.getId())
                .message("리액트러버님이 댓글에서 회원님을 언급했습니다.")
                .build());
        notificationRepository.save(Notification.builder()
                .user(user2).type(Notification.NotificationType.MENTION)
                .postId(p13.getId()).actorId(user3.getId())
                .message("자바킹님이 댓글에서 회원님을 언급했습니다.")
                .build());

        // 일부 알림은 이미 읽은 상태로 (자연스러움)
        // → 서비스 로직 없이 직접 read() 호출 불가하므로 기본 unread 유지

        // ============================================================
        // 신고 (소수의 부적절한 댓글 신고)
        // ============================================================
        // 자바vs파이썬 논쟁에서 과열된 댓글 신고
        reportRepository.save(Report.builder()
                .user(user3).comment(c1).reason(Report.ReportReason.ABUSE)
                .build());
        // 맥북 논쟁에서 비하 댓글 신고
        reportRepository.save(Report.builder()
                .user(user5).comment(c6).reason(Report.ReportReason.INAPPROPRIATE)
                .build());
    }

    // ============================================================
    // 헬퍼 메서드
    // ============================================================
    private void addView(User user, Post post) {
        viewLogRepository.save(new ViewLog(user, post));
        post.increaseViewCount();
    }

    private void addPostLike(User user, Post post) {
        postLikeRepository.save(new PostLike(user, post));
        post.increaseLikeCount();
    }

    private void addCommentLike(User user, Comment comment) {
        commentLikeRepository.save(new CommentLike(user, comment));
        comment.increaseLikeCount();
    }
}
