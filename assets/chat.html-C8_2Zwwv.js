import{_ as s,c as a,a as e,o as p}from"./app-TRw-0LGw.js";const l={};function t(c,n){return p(),a("div",null,[...n[0]||(n[0]=[e(`<h2 id="使用方式" tabindex="-1"><a class="header-anchor" href="#使用方式"><span>使用方式</span></a></h2><p>使用spring扫描实例, 实现<code>PipelineProcess</code>接口，指定泛型为<code>ChatMessagePipelineModel</code>。</p><blockquote><p>目前只有调用<code>sendChatMessageStream</code>才会触发相应聊天事件。</p></blockquote><div class="language-java line-numbers-mode" data-highlighter="prismjs" data-ext="java"><pre><code class="language-java"><span class="line"><span class="token annotation punctuation">@Slf4j</span></span>
<span class="line"><span class="token annotation punctuation">@Component</span></span>
<span class="line"><span class="token keyword">public</span> <span class="token keyword">class</span> <span class="token class-name">ChatInterceptor</span> <span class="token keyword">implements</span> <span class="token class-name">PipelineProcess</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> <span class="token punctuation">{</span></span>
<span class="line"></span>
<span class="line">    <span class="token doc-comment comment">/**</span>
<span class="line">     * 处理</span>
<span class="line">     *</span>
<span class="line">     * <span class="token keyword">@param</span> <span class="token parameter">context</span> 内容</span>
<span class="line">     */</span></span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">process</span><span class="token punctuation">(</span><span class="token class-name">PipelineContext</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> context<span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        log<span class="token punctuation">.</span><span class="token function">debug</span><span class="token punctuation">(</span><span class="token string">&quot;ChatInterceptor context:{}&quot;</span><span class="token punctuation">,</span> context<span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line"><span class="token punctuation">}</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><h2 id="自定义过滤" tabindex="-1"><a class="header-anchor" href="#自定义过滤"><span>自定义过滤</span></a></h2><blockquote><p>重写<code>support</code>方法，实现自定义过滤逻辑</p></blockquote><div class="language-java line-numbers-mode" data-highlighter="prismjs" data-ext="java"><pre><code class="language-java"><span class="line"></span>
<span class="line"><span class="token annotation punctuation">@Slf4j</span></span>
<span class="line"><span class="token annotation punctuation">@Component</span></span>
<span class="line"><span class="token keyword">public</span> <span class="token keyword">class</span> <span class="token class-name">ChatInterceptor</span> <span class="token keyword">implements</span> <span class="token class-name">PipelineProcess</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> <span class="token punctuation">{</span></span>
<span class="line"></span>
<span class="line">    <span class="token doc-comment comment">/**</span>
<span class="line">     * 是否支持</span>
<span class="line">     *</span>
<span class="line">     * <span class="token keyword">@param</span> <span class="token parameter">context</span> 内容</span>
<span class="line">     * <span class="token keyword">@return</span> 是否支持 true 支持 false 不支持</span>
<span class="line">     */</span></span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token keyword">boolean</span> <span class="token function">support</span><span class="token punctuation">(</span><span class="token class-name">PipelineContext</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> context<span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        <span class="token keyword">return</span> <span class="token string">&quot;message_end&quot;</span><span class="token punctuation">.</span><span class="token function">equals</span><span class="token punctuation">(</span>context<span class="token punctuation">.</span><span class="token function">getModel</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">getEvent</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line"></span>
<span class="line">    <span class="token doc-comment comment">/**</span>
<span class="line">     * 处理</span>
<span class="line">     *</span>
<span class="line">     * <span class="token keyword">@param</span> <span class="token parameter">context</span> 内容</span>
<span class="line">     */</span></span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">process</span><span class="token punctuation">(</span><span class="token class-name">PipelineContext</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> context<span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        log<span class="token punctuation">.</span><span class="token function">debug</span><span class="token punctuation">(</span><span class="token string">&quot;ChatInterceptor context:{}&quot;</span><span class="token punctuation">,</span> context<span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line"><span class="token punctuation">}</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div><h2 id="自定义执行顺序" tabindex="-1"><a class="header-anchor" href="#自定义执行顺序"><span>自定义执行顺序</span></a></h2><blockquote><p>重写<code>order</code>方法，返回排序值。越小越先执行。</p></blockquote><div class="language-java line-numbers-mode" data-highlighter="prismjs" data-ext="java"><pre><code class="language-java"><span class="line"></span>
<span class="line"><span class="token annotation punctuation">@Slf4j</span></span>
<span class="line"><span class="token annotation punctuation">@Component</span></span>
<span class="line"><span class="token keyword">public</span> <span class="token keyword">class</span> <span class="token class-name">ChatInterceptor</span> <span class="token keyword">implements</span> <span class="token class-name">PipelineProcess</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> <span class="token punctuation">{</span></span>
<span class="line"></span>
<span class="line">    <span class="token doc-comment comment">/**</span>
<span class="line">     * 获取排序，越小越靠前</span>
<span class="line">     *</span>
<span class="line">     * <span class="token keyword">@return</span> 排序</span>
<span class="line">     */</span></span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token class-name">Long</span> <span class="token function">order</span><span class="token punctuation">(</span><span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        <span class="token keyword">return</span> <span class="token number">233L</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line"></span>
<span class="line">    <span class="token doc-comment comment">/**</span>
<span class="line">     * 是否支持</span>
<span class="line">     *</span>
<span class="line">     * <span class="token keyword">@param</span> <span class="token parameter">context</span> 内容</span>
<span class="line">     * <span class="token keyword">@return</span> 是否支持 true 支持 false 不支持</span>
<span class="line">     */</span></span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token keyword">boolean</span> <span class="token function">support</span><span class="token punctuation">(</span><span class="token class-name">PipelineContext</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> context<span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        <span class="token keyword">return</span> <span class="token string">&quot;message_end&quot;</span><span class="token punctuation">.</span><span class="token function">equals</span><span class="token punctuation">(</span>context<span class="token punctuation">.</span><span class="token function">getModel</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">.</span><span class="token function">getEvent</span><span class="token punctuation">(</span><span class="token punctuation">)</span><span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line"></span>
<span class="line">    <span class="token doc-comment comment">/**</span>
<span class="line">     * 处理</span>
<span class="line">     *</span>
<span class="line">     * <span class="token keyword">@param</span> <span class="token parameter">context</span> 内容</span>
<span class="line">     */</span></span>
<span class="line">    <span class="token annotation punctuation">@Override</span></span>
<span class="line">    <span class="token keyword">public</span> <span class="token keyword">void</span> <span class="token function">process</span><span class="token punctuation">(</span><span class="token class-name">PipelineContext</span><span class="token generics"><span class="token punctuation">&lt;</span><span class="token class-name">ChatMessagePipelineModel</span><span class="token punctuation">&gt;</span></span> context<span class="token punctuation">)</span> <span class="token punctuation">{</span></span>
<span class="line">        log<span class="token punctuation">.</span><span class="token function">debug</span><span class="token punctuation">(</span><span class="token string">&quot;ChatInterceptor context:{}&quot;</span><span class="token punctuation">,</span> context<span class="token punctuation">)</span><span class="token punctuation">;</span></span>
<span class="line">    <span class="token punctuation">}</span></span>
<span class="line"><span class="token punctuation">}</span></span>
<span class="line"></span></code></pre><div class="line-numbers" aria-hidden="true" style="counter-reset:line-number 0;"><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div><div class="line-number"></div></div></div>`,10)])])}const o=s(l,[["render",t]]),u=JSON.parse('{"path":"/guide/event/chat.html","title":"聊天事件","lang":"zh-cn","frontmatter":{"lang":"zh-cn","title":"聊天事件","description":null},"git":{"updatedTime":1756186974000,"contributors":[{"name":"yanghq","username":"yanghq","email":"1040926235@qq.com","commits":1,"url":"https://github.com/yanghq"}],"changelog":[{"hash":"07306f604a0a726a52002288ae514c1f2972291e","time":1756186974000,"email":"1040926235@qq.com","author":"yanghq","message":"feat: chat message event"}]},"filePathRelative":"guide/event/chat.md"}');export{o as comp,u as data};
