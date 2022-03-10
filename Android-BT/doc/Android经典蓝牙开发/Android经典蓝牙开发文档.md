## 一、基本介绍
&#8195;&#8195;所谓蓝牙(Bluetooth)技术，实际上是一种短距离无线电技术，最初是由爱立信公司公司发明的。技术始于爱立信公司 1994 方案，它是研究在移动电话和其他配件间进行低功耗、低成本无线通信连接的方法。发明者希望为设备间的通讯创造一组统一规则（标准化协议）用来解决用户间相互不兼容的移动电子设备。  
&#8195;&#8195;1998年5月20日，索尼以立信、国际商业机器、英特尔、诺基亚及东芝公司等业界龙头创立“特别兴趣小组”（Special Interest Group SIG），即蓝牙技术联盟的前身，目标是开发一个成本低、效益高、可以在短距离范围内随意无线连接的蓝牙技术标准,是负责蓝牙规范的制定和推广的国际组织。  

![][01]

&#8195;&#8195;蓝牙发展至今经历了多个版本的更新，1.1、1.2、2.0、2.1、3.0、4.0、4.1、4.2、5.0等。其中，将1.x~3.0之间的版本称之为经典蓝牙，4.x开始的蓝牙称之为低功耗蓝牙，也就是蓝牙ble。根据应用、协议类型等，可以对蓝牙进行以下分类：    

![][02]

<br/>

## 二、经典蓝牙API介绍
&#8195;&#8195;Android平台包含蓝牙网络堆栈支持，此支持能让设备以无线方式与其他蓝牙设备交换数据。应用框架提供通过Android Bluetooth API访问蓝牙功能的权限。这些API允许应用以无线方式连接到其他蓝牙设备，从而实现点到点和多点无线功能。Android应用可通过Bluetooth API执行以下操作：

* 扫描其他蓝牙设备
* 查询本地蓝牙适配器的配对蓝牙设备
* 建立 RFCOMM 通道
* 通过服务发现连接到其他设备
* 与其他设备进行双向数据传输
* 管理多个连接

以下对经典蓝牙开发相关的API进行介绍：

#### 1、BluetoothAdapter类
&#8195;&#8195;BluetoothAdapter代表了移动设备的本地的蓝牙适配器, 通过该蓝牙适配器可以对蓝牙进行基本操作, 例如 : 启动设备发现，获取已配对设备，通过mac蓝牙地址获取蓝牙设备等。

#### （1）获取本地蓝牙适配器实例
方法定义：

	/**
	 * 作用：
	 *	获取本地蓝牙适配器实例
	 * 参数：
	 *	无
	 * 返回：
	 *	如果设备具备蓝牙功能，返回BluetoothAdapter 实例；否则，返回null对象。
	 */
	public static synchronized BluetoothAdapter getDefaultAdapter();

使用说明：

> 1、获取默认本地蓝牙适配器的句柄。目前Android仅支持一个蓝牙适配器，但该API可以扩展为支持更多。  

#### （2）打开蓝牙
方法定义：

	/**
	 * 作用：
	 *	打开蓝牙
	 * 参数：
	 *	无
	 * 返回：
	 *	如果蓝牙开始打开，则返回true；如果蓝牙打开发生问题，则返回false。
	 */
	public boolean enable();

使用说明：

> 1、需要 BLUETOOTH_ADMIN权限。  
> 2、该方法将不经过用户同意，直接启用底层蓝牙硬件，并启动所有蓝牙系统服务。由于不同Android设备系统的实现不同，所以部分Android系统在调用该方法时也会弹框请求用户同意。  
> 3、打开蓝牙，还可以通过调用startActivityForResult方法，使用ACTION_REQUEST_ENABLE意图来实现，此方法将弹出对话框，请求允许打开蓝牙。可以在Activity中的onActivityResult()方法中处理操作结果。  
> 4、该方法是一个异步调用：它将立即返回结果。如果此调用返回true，则适配器状态将立即从STATE_OFF转换为STATE_TURNING_ON，并且稍后过渡到STATE_OFF或STATE_ON 。如果此调用返回false，则说明出现问题阻止适配器开启，例如设备处于飞行模式，或者蓝牙已打开。因此还应当监听ACTION_STATE_CHANGED广播，以跟踪后续蓝牙状态更改。  

#### （3）关闭蓝牙
方法定义：

	/**
	 * 作用：
	 *	关闭蓝牙
	 * 参数：
	 *	无
	 * 返回：
	 *	如果蓝牙开始关闭，则返回true；如果蓝牙关闭发生问题，则返回false。
	 */
	public boolean disable();

使用说明：

> 1、需要 BLUETOOTH_ADMIN权限。  
> 2、该方法将不经过用户同意，关闭所有蓝牙连接，停止蓝牙系统服务并关闭底层蓝牙硬件。由于不同Android设备系统的实现不同，所以部分Android系统在调用该方法时也会弹框请求用户同意。  
> 3、该方法是一个异步调用：它将立即返回结果。如果此调用返回true，则适配器状态将立即从STATE_ON转换为STATE_TURNING_OFF，并且稍后过渡到STATE_OFF或STATE_ON 。如果此调用返回false，则说明出现问题阻止适配器关闭，例如适配器已关闭。因此还应当监听ACTION_STATE_CHANGED广播，以跟踪后续蓝牙状态更改。  

#### （4）验证蓝牙MAC地址
方法定义：

	/**
	 * 作用：
	 *	验证蓝牙设备MAC地址是否有效。
	 * 参数：
	 *	address：蓝牙MAC地址，字母必须大写，例如："00:43:A8:23:10:F1"。
	 * 返回：
	 *	如果蓝牙MAC地址有效，则返回true；否则返回false。
	 */
	public static boolean checkBluetoothAddress(String address);

使用说明：

> 无  

#### （5）获取本地蓝牙MAC地址
方法定义：

	/**
	 * 作用：
	 *	获取本地蓝牙适配器的硬件地址（MAC地址）
	 * 参数：
	 *	无
	 * 返回：
	 *	本地的硬件地址，例如："00:11:22:AA:BB:CC"。
	 */
	public String getAddress();

使用说明：

> 1、需要 BLUETOOTH权限。  

#### （6）获取蓝牙绑定列表
方法定义：

	/**
	 * 作用：
	 *	获取与本机蓝牙所有绑定的远程蓝牙信息。
	 * 参数：
	 *	无
	 * 返回：
	 *	将本地蓝牙适配器绑定的一组BluetoothDevice对象返回。若出现错误返回null。
	 */
	public Set<BluetoothDevice> getBondedDevices();

使用说明：

> 1、需要 BLUETOOTH权限。  
> 2、若蓝牙未打开，将返回空集。  

#### （7）获取蓝牙名称
方法定义：

	/**
	 * 作用：
	 *	获取本地蓝牙适配器的蓝牙名称。
	 * 参数：
	 *	无
	 * 返回：
	 *	本地蓝牙名称。若出现错误，返回null。
	 */
	public String getName();

使用说明：

> 1、需要 BLUETOOTH权限。  

#### （8）设置蓝牙名称
方法定义：

	/**
	 * 作用：
	 *	设置本地蓝牙适配器的蓝牙名称。
	 * 参数：
	 *	name：蓝牙名称。
	 * 返回：
	 *	设置成功返回true，否则返回false。
	 */
	public boolean setName(String name);

使用说明：

> 1、需要 BLUETOOTH_ADMIN权限。  
> 2、如果蓝牙未打开，该方法将返回false。  
> 3、尽管许多远程设备只能显示前40个字符，而有些可能仅限于20个，但有效的蓝牙名称最多使用UTF-8编码为248个字节。  

#### （9）获取蓝牙扫描模式
方法定义：

	/**
	 * 作用：
	 *	获取本地蓝牙适配器的当前蓝牙扫描模式。
	 * 参数：
	 *	无
	 * 返回：
	 *	当前蓝牙适配器的蓝牙扫描模式。
	 */
	public int getScanMode();

使用说明：

> 1、需要 BLUETOOTH权限。  
> 2、蓝牙扫描模式确定本地蓝牙适配器是否可被远程蓝牙设备连接和发现。  
> 3、如果蓝牙未打开，此方法将返回SCAN_MODE_NONE。  

蓝牙扫描模式： 

| 名称 | 值(int) | 含义 |
| ------------ | ------------ | ------------ |
| SCAN_MODE_NONE | 20 | 该设备不能扫描以及被扫描。 |
| SCAN_MODE_CONNECTABLE | 21 | 该设备可以扫描其他蓝牙设备。 |
| SCAN_MODE_CONNECTABLE_DISCOVERABLE | 23 | 该设备既可以扫描其他设备，也可以被其他设备扫描发现。 |

#### （10）获取蓝牙适配器状态
方法定义：

	/**
	 * 作用：
	 *	获取本地蓝牙适配器的当前状态。
	 * 参数：
	 *	无
	 * 返回：
	 *	当前蓝牙适配器状态。
	 */
	public int getState();

使用说明：

> 1、需要 BLUETOOTH权限。  

蓝牙适配器状态：

| 名称 | 值(int) | 含义 |
| ------------ | ------------ | ------------ |
| STATE_OFF | 10 | 表示本地蓝牙适配器已关闭 |
| STATE_TURNING_ON | 11 | 表示本地蓝牙适配器正在打开 |
| STATE_ON | 12 | 表示本地蓝牙适配器已开启，并可供使用 |
| STATE_TURNING_OFF | 13 | 表示本地蓝牙适配器正在关闭 |

#### （11）蓝牙是否打开
方法定义：

	/**
	 * 作用：
	 *	判断当前蓝牙适配器是否打开
	 * 参数：
	 *	无
	 * 返回：
	 *	若蓝牙为打开状态，则返回true，否则返回false。
	 */
	public boolean isEnabled();

使用说明：

> 1、需要 BLUETOOTH权限。  
> 2、如果蓝牙正处于打开状态并可用，则返回true值，getState()==STATE_ON 等价。  

#### （12）蓝牙是否正在扫描
方法定义：

	/**
	 * 作用：
	 *	判断蓝牙适配器是否正在处于扫描过程中。
	 * 参数：
	 *	无
	 * 返回：
	 *	若蓝牙处于扫描状态，则返回true；否则返回false。
	 */
	public boolean isDiscovering();

使用说明：

> 1、需要 BLUETOOTH权限。  
> 2、若蓝牙未打开，该方法将返回false。  
> 3、扫描设备是一个重量级过程，不应在扫描时尝试建立连接，而此时已存在的蓝牙连接将获得有限制的带宽以及高延迟。  

#### （13）启动扫描
方法定义：

	/**
	 * 作用：
	 *	开始扫描周边蓝牙设备。
	 * 参数：
	 *	无
	 * 返回：
	 *	若启动成功，返回true；否则返回false。
	 */
	public boolean startDiscovery();

使用说明：

> 1、需要 BLUETOOTH_ADMIN权限。  
> 2、通常为12秒左右的查询扫描过程。  
> 3、这是一个异步调用，它会立即返回。注册ACTION_DISCOVERY_STARTED和ACTION_DISCOVERY_FINISHED广播以确定发现何时开始和完成的确切时间。注册ACTION_FOUND以便在发现远程蓝牙设备时收到通知。  
> 4、若蓝牙未打开，该方法将返回false。  
> 5、扫描设备是一个重量级过程，不应在扫描时尝试建立连接，而此时已存在的蓝牙连接将获得有限制的带宽以及高延迟。可以使用cancelDiscovery()取消扫描操作。  

#### （14）取消扫描
方法定义：

	/**
	 * 作用：
	 *	取消蓝牙搜索操作
	 * 参数：
	 *	无
	 * 返回：
	 *	如果取消成功, 则返回true; 如果取消失败, 返回false。
	 */
	public boolean cancelDiscovery()

> 1、需要 BLUETOOTH_ADMIN权限。  
> 2、若蓝牙未打开，该方法将返回false。  
> 3、因为蓝牙搜索是一个重量级过程，会耗费大量资源，所以在连接远程蓝牙设备前，必须调用这个方法，取消搜索。  

#### （15）获取远程蓝牙设备
方法定义：

	/**
	 * 作用：
	 *	获取给定蓝牙硬件地址的BluetoothDevice对象。
	 * 参数：
	 *	address：蓝牙MAC地址，字母必须大写，例如："00:43:A8:23:10:F1"。
	 * 返回：
	 *	指定的远程蓝牙设备。
	 */
	public BluetoothDevice getRemoteDevice(String address);

使用说明：

> 1、如果MAC无效无效，将抛出IllegalArgumentException异常。  

#### （16）创建不安全的蓝牙服务套接字
方法定义：

	/**
	 * 作用：
	 *	创建一个正在监听的不安全的带有服务记录的无线射频通信（RFCOMM）蓝牙端口。
	 * 参数：
	 *	name：SDP记录下的服务器名，可以是任意字符串。
	 *	uuid：SDP记录下的UUID。
	 * 返回：
	 *	BluetoothServerSocket对象。
	 */
	public BluetoothServerSocket listenUsingInsecureRfcommWithServiceRecord(String name, UUID uuid);

使用说明：

> 1、需要 BLUETOOTH权限。  
> 2、系统将分配一个未使用的RFCOMM通道进行侦听。  
> 3、当发生错误时，例如蓝牙不可用、权限不足、通道被占用等，将抛出IOException异常。  
> 4、通过此方式创建的蓝牙服务套接字是不安全的，连接时不需要进行配对。  

#### （17）创建安全的蓝牙服务套接字
方法定义：

	/**
	 * 作用：
	 *	创建一个正在监听的安全的带有服务记录的无线射频通信（RFCOMM）蓝牙端口。
	 * 参数：
	 *	name：SDP记录下的服务器名，可以是任意字符串。
	 *	uuid：SDP记录下的UUID。
	 * 返回：
	 *	BluetoothServerSocket对象。
	 */
	public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID uuid);

使用说明：

> 1、需要 BLUETOOTH权限。  
> 2、系统将分配一个未使用的RFCOMM通道进行侦听。  
> 3、当发生错误时，例如蓝牙不可用、权限不足、通道被占用等，将抛出IOException异常。  
> 4、通过此方式创建的蓝牙服务套接字是安全的，连接时需要进行配对。  

<br/>

#### 2、BluetoothDevice类
&#8195;&#8195;BluetoothDevice对象代表了一个远程的蓝牙设备, 通过这个类可以查询远程设备的物理地址, 名称, 连接状态等信息。这个类实际上只是一个蓝牙硬件地址的简单包装，这个类的对象是不可变的。对这个类的操作, 会执行在远程蓝牙设备的硬件上。

#### （1）获取蓝牙名称
方法定义：

	/**
	 * 作用：
	 *	获取远程蓝牙设备的蓝牙名称。
	 * 参数：
	 *	无
	 * 返回：
	 *	成功则返回蓝牙名称，若出现问题则返回null。
	 */
	public String getName();

使用说明：

> 1、需要BLUETOOTH权限。  
> 2、执行设备扫描时，本地适配器将自动检索远程名称，并将缓存它们。此方法仅从缓存中返回此设备的名称。  

#### （2）获取蓝牙MAC
方法定义：

	/**
	 * 作用：
	 *	获取远程蓝牙设备的硬件地址
	 * 参数：
	 *	无
	 * 返回：
	 *	蓝牙硬件地址
	 */
	public String getAddress();

使用说明：

> 无  

#### （3）获取蓝牙绑定状态
方法定义：

	/**
	 * 作用：
	 *	获取远程蓝牙设备的绑定状态
	 * 参数：
	 *	无
	 * 返回：
	 *	蓝牙绑定状态
	 */
	public int getBondState();

使用说明：

> 1、需要BLUETOOTH权限。  

蓝牙绑定状态：

| 名称 | 值(int) | 含义 |
| ------------ | ------------ | ------------ |
| BOND_NONE | 10 | 远程设备未绑定。 |
| BOND_BONDING | 11 | 正在与远程设备进行绑定。 |
| BOND_BONDED | 12 | 远程设备已绑定。 |

#### （4）绑定远程设备
方法定义：

	/**
	 * 作用：
	 *	开始与远程蓝牙设备的绑定过程。
	 * 参数：
	 *	无
	 * 返回：
	 *	若成功开始绑定则返回true，否则返回false。
	 */
	public boolean createBond();

使用说明：

> 1、需要BLUETOOTH_ADMIN权限。  
> 2、这是一个异步调用，它会立即返回。注册监听ACTION_BOND_STATE_CHANGED广播，当绑定过程完成时及时获取其结果通知。  
> 3、Android系统服务将处理必要的用户交互以确认并完成绑定过程。  

#### （5）创建不安全的蓝牙套接字
方法定义：

	/**
	 * 作用：
	 *	创建不安全的蓝牙套接字。
	 * 参数：
	 *	uuid：用于查找RFCOMM通道的服务记录UUID
	 * 返回：
	 *	一个准备好外界连接的RFCOMM蓝牙服务端口
	 */
	public BluetoothSocket createInsecureRfcommSocketToServiceRecord(UUID uuid);

使用说明：

> 1、需要BLUETOOTH权限。  
> 2、通信渠道将不会有认证的链接密钥，即它将受到中间人攻击。  
> 3、对于蓝牙 2.1 设备，链接将被加密，因为加密是强制性的。对于旧设备（蓝牙 2.1 之前的设备），链接不会被加密。  
> 4、它旨在与listenUsingInsecureRfcommWithServiceRecord(String, UUID)用于对等蓝牙应用。  
> 5、出现错误时，例如蓝牙不可用、权限不足，将抛出IOException异常。  

#### （6）创建安全的蓝牙套接字
方法定义：

	/**
	 * 作用：
	 *	创建安全的蓝牙套接字。
	 * 参数：
	 *	uuid：用于查找RFCOMM通道的服务记录UUID
	 * 返回：
	 *	一个准备好外界连接的RFCOMM蓝牙服务端口
	 */
	public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid);

使用说明：

> 1、需要BLUETOOTH权限。  
> 2、只有经过身份验证的套接字链接才可以使用此套接字。认证是指认证链路密钥，以防止中间人攻击。  
> 3、此套接字上的通信将被加密。  
> 4、这是为与对等蓝牙应用程序 listenUsingRfcommWithServiceRecord(String, UUID)配合使用而设计的。  
> 5、出现错误时，例如蓝牙不可用、权限不足，将抛出IOException异常。  

<br/>

#### 3、BluetoothServerSocket类
&#8195;&#8195;BluetoothServerSocket是一个侦听蓝牙服务套接字。使用BluetoothServerSocket可以创建一个监听服务端口, 使用accept方法阻塞, 当该方法监测到连接的时候, 就会返回一个BluetoothSocket对象来管理这个连接。BluetoothServerSocket是线程安全的，close方法始终会立即中止正在进行的操作并关闭蓝牙服务套接字。需要BLUETOOTH权限。

#### （1）阻塞等待连接（无超时）
方法定义：

	/**
	 * 作用：
	 *	阻塞直到建立连接。
	 * 参数：
	 *	无
	 * 返回：
	 *	成功连接时连接的BluetoothSocket对象。
	 */
	public BluetoothSocket accept();

使用说明：

> 1、一旦这个调用返回，它可以被再次调用来接受后续的传入连接。  
> 2、close()可用于从另一个线程中止此调用。  
> 3、发生错误时，例如该调用被中止、超时，将抛出IOException异常。  

#### （2）阻塞等待连接（有超时）
方法定义：

	/**
	 * 作用：
	 *	阻塞直到建立连接或超时。
	 * 参数：
	 *	timeout：阻塞等待的超时时间。
	 * 返回：
	 *	成功连接时连接的BluetoothSocket对象。
	 */
	public BluetoothSocket accept(int timeout);

使用说明：

> 1、一旦这个调用返回，它可以被再次调用来接受后续的传入连接。  
> 2、close()可用于从另一个线程中止此调用。  
> 3、发生错误时，例如该调用被中止、超时，将抛出IOException异常。  

#### （3）关闭
方法定义：

	/**
	 * 作用：
	 *	关闭该监听服务端口，并释放所有关联的资源。
	 * 参数：
	 *	无
	 * 返回：
	 *	无
	 */
	public void close();

使用说明：

> 1、该方法将导致其他线程在此套接字上阻塞的调用立即引发IOException异常。  
> 2、关闭这个端口不会关闭accept()方法返回的BluetoothSocket对象。  
> 3、该方法调用出现问题，将抛出IOException异常。  

<br/>

#### 4、BluetoothSocket类
&#8195;&#8195;BluetoothSocket是蓝牙套接口。在服务器端，使用BluetoothServerSocket创建侦听服务器套接字。当连接被BluetoothServerSocket接受时，它将返回一个新的BluetoothSocket来管理连接。 在客户端，使用单个BluetoothSocket来启动连接并管理连接。最常见的蓝牙套接字类型是RFCOMM，它是Android API支持的类型。 RFCOMM是一种通过蓝牙实现的面向连接的流媒体传输。它也被称为串口行为规范（SPP）。BluetoothSocket是线程安全的，close方法始终会立即中止正在进行的操作并关闭套接字。需要BLUETOOTH权限。

#### （1）连接
方法定义：

	/**
	 * 作用：
	 *	尝试连接到远程蓝牙服务器。
	 * 参数：
	 *	无
	 * 返回：
	 *	无
	 */
	public void connect();

使用说明：

> 1、此方法将阻塞，直到建立连接或连接失败。 如果此方法没有异常返回，则此套接字现在已连接。  
> 2、该方法调用出现问题时，例如连接失败，将抛出IOException异常。  

#### （2）是否连接
方法定义：

	/**
	 * 作用：
	 *	获取此套接字的连接状态，即是否与远程蓝牙服务连接。
	 * 参数：
	 *	无
	 * 返回：
	 *	若连接则返回true，否则返回false。
	 */
	public boolean isConnected();

使用说明：

> 无  

#### （3）获取远程蓝牙设备
方法定义：

	/**
	 * 作用：
	 *	获取此套接字连接的远程蓝牙设备。
	 * 参数：
	 *	无
	 * 返回：
	 *	连接的远程蓝牙设备BluetoothDevice对象。
	 */
	public BluetoothDevice getRemoteDevice();

使用说明：

> 无  

#### （4）获取输入流
方法定义：

	/**
	 * 作用：
	 *	获取与此套接字关联的输入流。
	 * 参数：
	 *	无
	 * 返回：
	 *	输入流对象。
	 */
	public InputStream getInputStream();

使用说明：

> 1、即使套接字尚未连接，输入流也会返回，但对该流的操作将抛出IOException异常，直到关联的套接字连接。  
> 2、该方法调用出错时，将抛出IOException异常。  
> 3、通过此方法获取的输入流对象，可以读取对端发送的数据。  

#### （5）获取输出流
方法定义：

	/**
	 * 作用：
	 *	获取与此套接字关联的输出流。
	 * 参数：
	 *	无
	 * 返回：
	 *	输出流对象。
	 */
	public OutputStream getOutputStream();

使用说明：

> 1、即使套接字尚未连接，输出流也会返回，但对该流的操作将抛出IOException异常，直到关联的套接字连接。  
> 2、该方法调用出错时，将抛出IOException异常。  
> 3、通过此方法获取的输出流对象，可以发送数据给对端。  

#### （6）关闭
方法定义：

	/**
	 * 作用：
	 *	关闭此流并释放与其关联的所有系统资源。如果流已经关闭，则调用此方法不起作用。
	 * 参数：
	 *	无
	 * 返回：
	 *	无
	 */
	public void close();

使用说明：

> 1、该方法调用出现问题，将抛出IOException异常。  

<br/>

## 三、经典蓝牙开发流程
### 1、经典蓝牙开发流程分析

![][03]

### 2、蓝牙服务端实现
##### (1)在工程清单文件AndroidManifest.xml中添加权限：

	<!--如果使用了BLUETOOTH_ADMIN权限，那么必须使用BLUETOOTH权限-->
	<uses-permission android:name="android.permission.BLUETOOTH" />
	<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>

	<!--android6.0后需要搜索周边蓝牙设备，需要添加以下两个权限-->
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

	<!--要求设备硬件必须支持蓝牙-->
    <uses-feature android:name="android.hardware.bluetooth" android:required="true"/>

##### (2)获取本地蓝牙适配器：

	BluetoothAdapter mAdapter= BluetoothAdapter.getDefaultAdapter();

##### (3)打开蓝牙：

	//方式一：通过Intent来向用户弹框请求打开蓝牙，可以重写onActivityResult来监听打开蓝牙的请求结果
	//打开蓝牙
    public void openBluetooth(){
        if(mBluetoothAdapter==null){
			//自定义方法，用来往TextView上添加提示信息
            showTip("当前设备不支持蓝牙功能！");
            return;
        }

        if(mBluetoothAdapter.isEnabled()){
            showTip("蓝牙已打开");
            return;
        }

        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent,GlobalDef.REQ_CODE_OPEN_BT);
    }

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GlobalDef.REQ_CODE_OPEN_BT){
            if(resultCode == Activity.RESULT_OK){
                showTip("蓝牙打开成功");
            }
            else{
                showTip("蓝牙打开失败");
            }
        }
    }

	//方式二：通过enable方法静默打开蓝牙，无需用户同意（部分Android系统使用该方法依然会弹框提示，向用户请求打开蓝牙）
	mBluetoothAdapter.enable();

##### (4)关闭蓝牙

	//关闭蓝牙，无需用户同意（部分Android系统使用该方法依然会弹框提示）
	mBluetoothAdapter.disable();

##### (5)允许蓝牙可见：

	//方式一：通过Intent方式向用户请求允许蓝牙被搜索
	//注：如果蓝牙没有开启，用户点击确定后，会首先开启蓝牙，继而设置蓝牙能被扫描
    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    //设置蓝牙可见性的时间，默认持续时间为120秒，每个请求的最长持续时间上限为300秒
    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
    startActivity(intent);

	//方式二：通过反射的方式来设置蓝牙可见性，且不会出现弹框
	//注：如果蓝牙没有开启，通过此方式并不会直接打开蓝牙
	/**
     * 设置蓝牙可见
     * @param adapter
     * @param timeout 超时为0时，永久可见
     */
    public static void setDiscoverableTimeout(BluetoothAdapter adapter, int timeout) {
        //BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭蓝牙可见
     * @param adapter
     */
    public static void closeDiscoverableTimeout(BluetoothAdapter adapter) {
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

##### (6)创建蓝牙服务套接字，等待其他蓝牙客户端连接：

	try{
		mSocketList=new LinkedList<BluetoothSocket>();//用来管理连接的蓝牙套接字
        mExecutorService= Executors.newCachedThreadPool();//创建线程池
		//创建蓝牙服务端
        mServerSocket=mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothTool", GlobalDef.BT_UUID);
        mServerRunningFlag=true;

        showTip("蓝牙服务端成功启动");
        new Thread(){
			@Override
            public void run(){
				try{
					BluetoothSocket socket=null;
					//循环等待蓝牙socket连接
                    while(mServerRunningFlag){
						socket=mServerSocket.accept();//阻塞式
                        mSocketList.add(socket);
						//SocketThread为自定义的线程类，用于管理BluetoothSocket的读写操作
                        mExecutorService.execute(new SocketThread(socket));
                    }
				}catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
	}catch(IOException e){
        e.printStackTrace();
        showTip("服务端启动出现异常");
        Log.e(TAG,"runServer IOException");
    }

##### (7)连接成功之后，就通过获取BluetoothSocket的输入输出流进行数据传输：

	// 获取流
	InputStream inputStream = socket.getInputStream();
	OutputStream outputStream = socket.getOutputStream();
	// 写出、读入
	byte[] temp=new byte[1024];
	inputStream.read(temp);//当无数据时将阻塞等待
	outputStream.write(temp);

##### (8)以下为用于操作BluetoothSocket的SocketThread的简单实现，仅供参考：


	class SocketThread extends Thread {
        private BluetoothSocket mSocket=null;
        private InputStream mIn;
        private OutputStream mOut;
        private boolean isOpen = false;

        public SocketThread(BluetoothSocket socket) {
            try {
                mSocket=socket;
                mIn = mSocket.getInputStream();
                mOut = mSocket.getOutputStream();
                isOpen = true;
                Log.d(TAG, "a socket thread create");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "create SocketThread fail");
            }
        }

        @Override
        public void run() {
            int readLen=0;
            byte[] buffer=new byte[1024];
            try{
                while(isOpen){
                    readLen=mIn.read(buffer);
                    if(readLen>0){
                        Log.i(TAG,"read data length="+readLen);
						Log.i(TAG,"read data hex = "+StringUtil.bytesToHexString(buffer,0,readLen));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                release();
            }
        }

        /**
         * 写入数据
         * @param data
         * @param offset
         * @param len
         */
        public void writeData(byte[] data,int offset,int len){
            if (data == null || offset<0 || len<=0 || (len+offset)>data.length) {
                Log.e(TAG,"BT writeData params fail");
                return;
            }

            try {
				mOut.write(data,offset,len);
                mOut.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

		public void release(){
            Log.d(TAG,"A socketThread release");
            try{
                isOpen=false;

                if(mOut!=null){
                    try{
                        mOut.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mOut=null;
                }
                if(mIn!=null){
                    try{
                        mIn.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mIn=null;
                }
                if(mSocket!=null){
                    try{
                        mSocket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mSocket=null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

### 3、蓝牙客户端实现
##### (1)添加权限，同蓝牙服务端。

##### (2)获取本地蓝牙适配器，同蓝牙服务端。

##### (3)打开蓝牙，同蓝牙服务端。

##### (4)关闭蓝牙，同蓝牙服务端。

##### (5)允许蓝牙可见，同蓝牙服务端。

##### (6)定义蓝牙广播接收器，用于接收蓝牙搜索、连接状态改变等的广播：

	class BluetoothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String action=intent.getAction();
            Log.d(TAG,"Action received is "+action);
            //蓝牙搜索
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(scanDevice == null || scanDevice.getName() == null){
                    return;
                }

                int btType=scanDevice.getType();
                if(btType==BluetoothDevice.DEVICE_TYPE_LE || btType==BluetoothDevice.DEVICE_TYPE_UNKNOWN){
                    return;
                }

                Log.d(TAG, "bt name="+scanDevice.getName()+" address="+scanDevice.getAddress());
				//将搜索到的蓝牙设备加入列表
                deviceList.add(scanDevice);
                short rssi=intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                rssiList.add(rssi);
                listAdapter.notifyDataSetChanged();//通知ListView适配器更新
            }
            //蓝牙配对
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mCurDevice!=null && btDevice.getAddress().equals(mCurDevice.getAddress())){
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    if(state==BluetoothDevice.BOND_NONE){
                        showTip("已取消与设备" + btDevice.getName() + "的配对");
                        mFlag=-1;
                    }
                    else if(state==BluetoothDevice.BOND_BONDED){
                        showTip("与设备" + btDevice.getName() + "配对成功");
                        mFlag=1;
                    }
                }
            }
            else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG,"onReceive---------STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG,"onReceive---------STATE_ON");
                        showTip("蓝牙当前状态：ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG,"onReceive---------STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG,"onReceive---------STATE_OFF");
                        showTip("蓝牙当前状态：OFF");
                        break;
                }
            }
        }
    }

##### (7)注册广播：

        mBluetoothBroadcastReceiver=new BluetoothBroadcastReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothBroadcastReceiver,filter);

##### (8)搜索周边蓝牙设备：

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
		//搜索到的蓝牙设备通过广播接收
        mBluetoothAdapter.startDiscovery();

##### (9)建立与蓝牙服务器的连接：

	/**
     * 蓝牙配对并连接
     */
    public void bondAndConnect(BluetoothDevice mCurDevice){
        //取消搜索
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }

        if(mCurDevice==null){
            showTip("远程蓝牙设备为空！");
            return;
        }

        //当前蓝牙设备未配对，则先进行配对
        if(mCurDevice.getBondState()==BluetoothDevice.BOND_NONE){
            Log.d(TAG,"create bond to "+mCurDevice.getName());
            boolean nRet= BluetoothUtil.createBond(mCurDevice);
            if(!nRet){
                showTip("createBond fail！");
                return;
            }
            showLoadingDialog("正在与【"+mCurDevice.getName()+"】进行配对...");
            mFlag=0;
            while(mFlag==0){
                SystemClock.sleep(250);
            }
            if(mFlag==-1){
                showTip("与【"+mCurDevice.getName()+"】的蓝牙配对失败");
                dismissLoadingDialog();
                return;
            }
        }

        if(mCurDevice.getBondState()==BluetoothDevice.BOND_BONDED){
            showLoadingDialog("正在与【"+mCurDevice.getName()+"】进行连接...");
            try {
                //创建Socket
                BluetoothSocket socket = mCurDevice.createRfcommSocketToServiceRecord(GlobalDef.BT_UUID);
                //连接蓝牙服务套接字
                socket.connect();
                mThread=new SocketThread(socket);
                mThread.start();
                showTip(("成功与【"+mCurDevice.getName()+"】建立连接"));
            } catch (IOException e) {
                Log.d(TAG,"socket connect fail");
                showTip(("连接【"+mCurDevice.getName()+"】失败"));
                e.printStackTrace();
            }
        }
        dismissLoadingDialog();
    }

##### (10)连接成功之后，就通过输入输出流进行数据传输，同蓝牙服务端。

<br/>

## 四、注意事项与常见问题
##### 1、Android6.0以后，搜索蓝牙设备需要位置权限（危险权限，需要动态申请）。 
##### 2、高版本Android系统上进行蓝牙搜索时，除了动态申请位置权限外，有的可能还需要手动打开设备的位置信息，否则无法搜索蓝牙。

![][04]

##### 3、搜索周边蓝牙设备时，本机并不需要处于蓝牙可见状态。但其他设备必须处于蓝牙可见状态，本机才可以搜索到。已知蓝牙设备（处于有效范围内）的MAC地址，则随时可以向其发起连接，而无需打开蓝牙可见性。
##### 4、如果尚未在设备上启用蓝牙，则启用设备可检测性将会自动启用蓝牙。  
##### 5、搜索周边设备对于蓝牙适配器而言是一个非常繁重的操作过程，并且会消耗大量资源。在找到要连接的设备后，确保始终使用 cancelDiscovery() 停止发现，然后再尝试连接。 此外，如果已经保持与某台设备的连接，那么执行搜索操作可能会大幅减少可用于该连接的带宽，因此不应该在处于连接状态时执行搜索周边蓝牙的操作。  
##### 6、在调用 connect() 时，应始终确保设备未在进行搜索操作。 如果正在进行搜索操作，则会大幅降低连接尝试的速度，并增加连接失败的可能性。
##### 7、搜索周边设备，通过广播能够获取到周边蓝牙设备的名称、mac地址、rssi信号强度等。
##### 8、BluetoothSocket是线程安全的, close()方法会终止BluetoothSocket进行的一切操作, 并且同时会关闭连接。
##### 9、经典蓝牙是通过流来进行数据收发的，对流进行数据读取操作时，由于接收方不知道消息之间的界限，不知道一次性提取多少字节的数据，因而容易产生数据粘包问题。对于这种问题，可以给每个完整的数据包添加一个起始符与结束符，那么接收方就可以确定需要读取并处理的数据范围。



<br/>

## 五、附录
1. [深入了解Android蓝牙Bluetooth——《基础篇》](https://blog.csdn.net/androidstarjack/article/details/60468468 "深入了解Android蓝牙Bluetooth——《基础篇》")
2. [Android 经典蓝牙开发](https://blog.csdn.net/jdfkldjlkjdl/article/details/84637354 "Android 经典蓝牙开发")
3. [Android蓝牙开发—经典蓝牙详细开发流程](https://blog.csdn.net/zqf_888/article/details/81060606 "Android蓝牙开发—经典蓝牙详细开发流程")  
4. [蓝牙概览](https://developer.android.google.cn/guide/topics/connectivity/bluetooth "蓝牙概览")







[01]: ./pic/1.png  
[02]: ./pic/2.png
[03]: ./pic/3.png
[04]: ./pic/4.png








