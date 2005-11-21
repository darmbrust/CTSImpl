/*  Function for highlighting TOC items ****************************************/
function highlightRow(row,toggle){
    if(document.all){

        toggle == 1 ? row.style.background = "990000" : row.style.background = "000000";
        //toggle == 1 ? row.style.background = "7a8cad" : row.style.background = "43527a";
    }
}

/*  Funciton for toggling TOC items *******************************************/
function toggle(elem)
{   
	if(elem!=null)//Temp: added 12-12-00; MLWardrick; error-handling for null objects
	{
		if(elem.nextSibling!=null) {
			elem.nextSibling.style.display = ("" == elem.nextSibling.style.display ? "none" : "");
	    	elem.children[0].children[0].innerHTML = ("6" == elem.children[0].children[0].innerHTML ? "4" : "6");
		};
	}
}