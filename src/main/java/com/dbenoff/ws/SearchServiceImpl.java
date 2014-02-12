package com.dbenoff.ws;

import com.itasoftware.nrhp.SearchResult;
import com.dbenoff.nrhp.impl.PropertyImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

public class SearchServiceImpl implements SearchService {
	
       @Path("/property/{searchterm}/{page}")
	   @GET
       @Produces("application/json")
       @Override
	   public SearchResult searchProperties(@PathParam("searchterm") String searchterm, @PathParam("page") int page) {
		   SearchResult result = PropertySearchFactory.search(searchterm, page);
		   for(int i = 0; i < result.getProperties().length; i++){
			   PropertyImpl prop = (PropertyImpl)result.getProperties()[i];
			   prop = (PropertyImpl) prop.clone();  //create a clone that we can apply highlighting to
			   result.getProperties()[i] = prop;
			   String[] names = prop.getNames();
			   for(int j = 0; j < names.length; j++){
				   names[j] = this.wrap(names[j], searchterm);
			   }
			   prop.setNames(names);
			   prop.setAddress(this.wrap(prop.getAddress(), searchterm));
			   prop.setCity(this.wrap(prop.getCity(), searchterm));
			   prop.setState(this.wrap(prop.getState(), searchterm));
		   }
		   return result;
	   }
	   
		/**
		 * @param text string potentially containing search terms
		 * @param search	the substring to search for
		 * @return string with search wrapped in span tags, including interspersed 
		 * 		whitespace and punctuation
		 */
		private String wrap(String text, String search){

            if(text == null)
                return text;

			search = search.replaceAll("[^a-zA-Z0-9]", "");
			
			int startIndex = -1;
			int searchIndex = 0;
			
			for(int i = 0; i < text.length(); i++){
				char textChar = text.charAt(i);
				char searchChar = search.charAt(searchIndex);
				if(Character.isLetterOrDigit(text.charAt(i))){
					if(String.valueOf(textChar).equalsIgnoreCase(String.valueOf(searchChar))){
						if(startIndex < 0){
							startIndex = i;
						}
						searchIndex++;
						if(searchIndex == search.length()){
							String start = text.substring(0, startIndex);
							String mid = text.substring(startIndex, i + 1);
							String end = text.substring(i + 1);
							text = start + "<span class=\"search\">"
										+ mid
										+ "</span>"
										+ end;
							i = i + mid.length() + 22;
							startIndex = -1;
							searchIndex = 0;
						}
					}else{
						startIndex = -1;
						searchIndex = 0;
					}
				}
			}
			return text;
			
		}
}
