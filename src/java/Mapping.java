public class Mapping{
	char[] key;
	
	public Mapping() {
		key = new char[26];
	}
	
	public Mapping(String cipher, String translation) {
		key = new char[26];

		for(int i = 0; i < translation.length(); i++)
		{
			key[(int)cipher.charAt(i)-(int)'A'] = translation.charAt(i);
		}
		//randomMapping();
	}
	
	public Mapping(Mapping m){
		key = new char[26];
		
		for(int i = 0; i < 26; i ++)
		{
			key[i] = m.getKey()[i];
		}
	}
	
	public char[] getKey() {
		return key;
	}
	
	public void setKey(char[] newKey) {
		key = newKey;
	}
	
	public void print() {
		for(int i = 0; i < 26; i ++)
		{
			System.out.print(key[i]);
		}
		System.out.println();
	}
}
