package cn.wang.controller;
import cn.wang.dto.LoginInfo;
import cn.wang.dto.UserAddDTO;
import cn.wang.dto.UserListDTO;
import cn.wang.dto.UserUpdateDTO;
import cn.wang.exception.BackendClientException;
import cn.wang.po.User;
import cn.wang.service.UserServiceImpl;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.xml.bind.DatatypeConverter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

   @Value("${spring.mail.username}")
    private String sendAddress;
    @Autowired
    private RedisTemplate redisTemplate;




    @GetMapping("/getById")
    public User getById(@RequestParam Long userId){
        User user = userService.getById(userId);
        return user;
    }
    @GetMapping("/getCurrentUserInfo")
    public User getCurrentUser(@RequestAttribute Long userId,@RequestParam(name = "abc") String abc){
        User currentUser = userService.getById(userId);

        return currentUser;
    }

    @PostMapping("/add")
    public void add(@RequestBody UserAddDTO userAddDTO){
        userService.add(userAddDTO);
    }

    @GetMapping("/login")
    public String login(String username,String password) throws BackendClientException {
        User user = userService.getByUsername(username);
        if (user==null){
            throw new BackendClientException("user doesn't exist");
        }
        String s = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getEncryptedPassword().equals(s)){
            throw new BackendClientException("password is invalid");
        };
        LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getUsername(), user.getRoles(), new Date());
        String loginInfoStr = JSON.toJSONString(loginInfo);
        Base64.getEncoder().encodeToString(loginInfoStr.getBytes());
        String token = Base64.getEncoder().encodeToString(loginInfoStr.getBytes());
        return token;
    }

    @GetMapping("/resetPassword")
    public void resetPassword(@RequestParam @Email String email){
        //加密的随机数
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = secureRandom.generateSeed(4);
        //printHexBinary：将一个byte[]数组编码成一个String类型  (DatatypeConverter是final类所以不能被继承)
        String code = DatatypeConverter.printHexBinary(bytes);
        //发送邮件到邮箱（只能用来发送text格式的邮件）
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sendAddress);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("JCart Email Verify Code");
        String url = String.format("您本次的验证码为"+code+" 请尽快验证");
        simpleMailMessage.setText(url);
        //如果出现554就用
       // simpleMailMessage.setCc(sendAddress);
        javaMailSender.send(simpleMailMessage);
        //发送邮件的推送功能
       redisTemplate.opsForValue().set(email,code,10*60, TimeUnit.SECONDS);
        //操作字符串

    }

    //验证码
    @GetMapping("/verifyCode")
    public void verifyEmaliCode(@RequestParam @Email String email,@RequestParam String code) throws BackendClientException {
        //从Redis中取
        String redisCode = (String) redisTemplate.opsForValue().get(email);
        if (redisCode==null){
            throw new BackendClientException("email verify code is expire");
        }
        if (!redisCode.equals(code)){
            throw new BackendClientException("email verify code id invalid");
        }
        userService.changeUserPasswordByEmail(email,"123456");
        //自定修改，返回token，授权一个修改密码的api
    }


    @PostMapping("/update")
    public void update(@RequestBody UserUpdateDTO userUpdateDTO){
        userService.update(userUpdateDTO);
    }



    @GetMapping("/getUserWithPage")
    public PageInfo<UserListDTO> getUserListDTO(@RequestParam(required = false,defaultValue = "1")Integer pageNum){
        PageInfo<UserListDTO> usersWithPage = userService.getUsersWithPage(pageNum);
        return usersWithPage;
    }

    @PostMapping("/batchDelete")
    public void batchDelete(@RequestBody List<Integer> userIds,@RequestAttribute Integer currentIds) throws BackendClientException {
        boolean contains = userIds.contains(currentIds);
        if (contains){
            throw new BackendClientException("cannot delete current user");
        }
        userService.batchDelete(userIds);
    }

    @PostMapping("/uploadAvatar")
    public void uploadAvatar(@RequestBody String avatarData) throws IOException {
        String[] split = avatarData.split(",");
        String type = split[0].split(";")[0].split("/")[1];
        byte[] imgBytes = Base64.getDecoder().decode(split[1]);
        //生产随机数然后转String类型
        String uuid = UUID.randomUUID().toString();
        //String类的format()方法用于创建格式化的字符串以及连接多个字符串对象
        String url = String.format("avatarimg/%s.%s", uuid, type);
        storeAvatar(imgBytes,url);

    }


    @PostMapping("/uploadAvatar2")
    public String uploadAvatar(@RequestParam("file")MultipartFile file) throws BackendClientException, IOException {
        //file. get content type文件。得到的内容类型的意思
        String contentType = file.getContentType();
        if (!contentType.equals("image/png")&&!contentType.equals("image/jpg")){
            throw new BackendClientException("file only support pong or jpg");
        }
        String uuid = UUID.randomUUID().toString();
        String type = file.getContentType();
        type=type.split("/")[1];
        String fileName = String.format("%s.%s", uuid, type);
        String url = String.format("avatarimg/%s", fileName);
        storeAvatar(file.getBytes(),url);
        return fileName;
    }

    private void storeAvatar(byte[] imgDate,String fileName) throws IOException {
        //pw = new OutputStreamWriter(new FileOutputStream(“D:/test.txt”),"GBK");//确认流的输出文件和编码格式，此过程创建了“test.txt”实例 。
        //
        //pw.write("是要写入到记事本文件的内容");//将要写入文件的内容，可以多次write pw.close();//关闭流 。
        //
        //备注：文件流用完之后必须及时通过close方法关闭，否则会一直处于打开状态，直至程序停止，增加系统负担。
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(imgDate);
        out.close();
    }


}
