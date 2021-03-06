# Protobuf4j

Protobuf4j is a small and fast  serialization and deserialization java lib for google protobuf protocol.

# Protobuf4j是一个小巧JAVA版的protobuf序列化和反序列化库，它具有一下特性：

1，接近原生的protoc工具生成的java代码的序列化和反序列化速度，综合测试下来是jprotobuf效率的2-4倍。<br/>
2，支持预编译，预编译的速度也很快，相比jprotobuf提升了不少。<br/>
3，支持Set,Map集合<br/>

# 注意事项

1，协议类如果打算被继承，访问修饰符必须是大于等于protected,(基于性能考虑，序列化反序列化的操作不使用反射，通常我们推荐使用聚合的方式来代替继承）。<br/>
2，preCompile系列方法必须在项目启动时候最优先执行。<br/>

# maven使用方式

  &lt;dependency&gt;<br/>
    	  &lt;groupId&gt; com.game37 &lt;/groupId&gt;<br/>
		  &lt;artifactId&gt; protobuf4j &lt;/artifactId&gt;<br/>
		  &lt;version&gt; 1.0.0 &lt;/version&gt;<br/>
  &lt;/dependency><br/>
  
# 定义协议类

@ProtobufClass<br/>
public class UserResp {
	
	@ProtobufField
	private long id;
	@ProtobufField
	private String name;
		
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "UserResp [id=" + id + ", name=" + name + "]";
	}
}
  
# 直接使用方式

<br/>
  private static void testSimple() throws Exception {
		
		String targetFullClassName = "pack.UserResp";
		ClassModifyUtil.preCompile(targetFullClassName);
		
		UserResp user = new UserResp();
		user.setId(666L);
		user.setName("你好protobuf4j!");
		
		ProtobufCodec<UserResp> codec = (ProtobufCodec<UserResp>)user;
		byte[] data = codec.encode();
		UserResp userNew = codec.decode(data);
		
		System.out.println(user);
		System.out.println(userNew);
				
	}
  
  # 使用包路径扫描的方式
  
  <br/>
  private static void testPackScan() throws Exception {
		
		
		List<String> packSearchList = new ArrayList<>(1);
		packSearchList.add("pack");
		
		Set<String>  annoClassSet = new HashSet<String>(2);
		annoClassSet.add(ProtobufClass.class.getName());
		annoClassSet.add(ProtobufField.class.getName());
		
		ClassModifyUtil.preCompileByPackScan(packSearchList, annoClassSet);
		
		UserResp user = new UserResp();
		user.setId(666L);
		user.setName("你好protobuf4j!");
		
		ProtobufCodec<UserResp> codec = (ProtobufCodec<UserResp>)user;
		byte[] data = codec.encode();
		UserResp userNew = codec.decode(data);
		
		System.out.println(user);
		System.out.println(userNew);
				
	}


