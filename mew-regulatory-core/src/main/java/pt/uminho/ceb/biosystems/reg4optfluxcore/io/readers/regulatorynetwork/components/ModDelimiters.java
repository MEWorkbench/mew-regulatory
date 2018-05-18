package pt.uminho.ceb.biosystems.reg4optfluxcore.io.readers.regulatorynetwork.components;

public enum ModDelimiters {
	
	      SEMICOLON{
				public String toString() {
					return "Semi-colon (;)";
				}
				
				public String getDelimiter() {
					return ";";
				}
			},
			COMMA{
				public String toString() {
					return "Comma (,)";
				}
				
				public String getDelimiter() {
					return ",";
				}
			},
			TAB{
				public String toString() {
					return "Tab (\\t)";
				}
				
				public String getDelimiter() {
					return "\t";
				}
			},
			
/*			WHITE_SPACE{
				public String toString() {
					return "White Space ( )";
				}
				
				public String getDelimiter() {
					return " ";
				}
			},*/
			COLON{
				public String toString() {
					return "Colon (:)";
				}
				
				public String getDelimiter() {
					return ":";
				}
			},
			ATSIGN{
				public String toString() {
					return "At sign (@)";
				}
				
				public String getDelimiter() {
					return "@";
				}
			},
	
			EQUALS {
				public String toString() {
					return "Equals (=)";
					
				}
				
				public String getDelimiter(){
					return "=";
				}
				
				
			};

			
			public String getDelimiter(){
				return this.getDelimiter();
			}
			
			
}
