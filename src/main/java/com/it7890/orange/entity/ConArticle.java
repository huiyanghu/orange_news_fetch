package com.it7890.orange.entity;
import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;


@AVClassName("conarticle")
public class ConArticle extends AVObject {

	public ConArticle() {
		super();
	}

	public String getPlogo() {
		return getString("plogo");
	}

	public void setPlogo(String plogo) {
		this.put("plogo",plogo);
	}

	public String getAuthorheadimg() {
		return getString("authorheadimg");
	}

	public void setAuthorheadimg(String authorheadimg) {
		this.put("authorheadimg",authorheadimg);
	}

	public String getTitlepic() {
		return getString("titlepic");
	}

	public void setTitlepic(String titlepic) {
		this.put("titlepic",titlepic);
	}

	public String getSourcetitilepic() {
		return getString("sourcetitilepic");
	}

	public void setSourcetitilepic(String sourcetitilepic) {
		this.put("sourcetitilepic",sourcetitilepic);
	}

	public long getId() {
		return getLong("id");
	}

	public void setId(long id) {
		this.put("id",id);
	}

	public String getAbstracts() {
		return getString("abstracts");
	}

	public void setAbstracts(String abstracts) {
		this.put("abstracts",abstracts);
	}

	public String getArticleid() {
		return getString("articleid");
	}

	public void setArticleid(String articleid) {
		this.put("articleid",articleid);
	}

	public int getAttr() {
		return getInt("attr");
	}

	public void setAttr(int attr) {
		this.put("attr",attr);
	}

	public String getAuthor() {
		return getString("author");
	}

	public void setAuthor(String author) {
		this.put("author",author);
	}

	public int getChannelid() {
		return getInt("channelid");
	}

	public void setChannelid(int channelid) {
		this.put("channelid",channelid);
	}

	public String getCountrycode() {
		return getString("countrycode");
	}

	public void setCountrycode(String countrycode) {
		this.put("countrycode",countrycode);
	}

	public int getCreateuid() {
		return getInt("createuid");
	}

	public void setCreateuid(int createuid) {
		this.put("createuid",createuid);
	}

	public int getCtype() {
		return getInt("ctype");
	}

	public void setCtype(int ctype) {
		this.put("ctype",ctype);
	}

	public int getPushnum() {
		return getInt("pushnum");
	}

	public void setPushnum(int pushnum) {
		this.put("pushnum",pushnum);
	}

	public String getKeywords() {
		return getString("keywords");
	}

	public void setKeywords(String keywords) {
		this.put("keywords",keywords);
	}

	public String getLangid() {
		return getString("langid");
	}

	public void setLangid(String langid) {
		this.put("langid",langid);
	}

	public int getLatitude() {
		return getInt("latitude");
	}

	public void setLatitude(int latitude) {
		this.put("latitude",latitude);
	}

	public String getLinkurl() {
		return getString("linkurl");
	}

	public void setLinkurl(String linkurl) {
		this.put("linkurl",linkurl);
	}

	public int getLongitude() {
		return getInt("longitude");
	}

	public void setLongitude(float longitude) {
		this.put("longitude",longitude);
	}

	public String getMedialink() {
		return getString("medialink");
	}

	public void setMedialink(String medialink) {
		this.put("medialink",medialink);
	}

	public int getPublicationid() {
		return getInt("publicationid");
	}

	public void setPublicationid(int publicationid) {
		this.put("publicationid",publicationid);
	}

	public int getRank() {
		return getInt("rank");
	}

	public void setRank(int rank) {
		this.put("rank",rank);
	}

	public String getSource() {
		return getString("source");
	}

	public void setSource(String source) {
		this.put("source",source);
	}

	public String getSourceurl() {
		return getString("sourceurl");
	}

	public void setSourceurl(String sourceurl) {
		this.put("sourceurl",sourceurl);
	}

	public int getStatus() {
		return getInt("status");
	}

	public void setStatus(int status) {
		this.put("status",status);
	}

	public int getSubuid() {
		return getInt("subuid");
	}

	public void setSubuid(int subuid) {
		this.put("subuid",subuid);
	}

	public String getTitle() {
		return getString("title");
	}

	public void setTitle(String title) {
		this.put("title",title);
	}

	public int getTopicsid() {
		return getInt("topicsid");
	}

	public void setTopicsid(int topicsid) {
		this.put("topicsid",topicsid);
	}

	public String getWriter() {
		return getString("writer");
	}

	public void setWriter(String writer) {
		this.put("writer",writer);
	}

	public int getImgcount() {
		return getInt("imgcount");
	}

	public void setImgcount(int imgcount) {
		this.put("imgcount",imgcount);
	}
}