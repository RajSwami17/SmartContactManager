console.log("this is script file");

const tooggleSidebar=()=>{

	if($(".sidebar").is(":visible"))
	{
		//true
		//to hide sidebar
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}
	else
	{
		//false
		//to show sidebar
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};

const searchContact=()=>
{
	//console.log("Searching....");
	let query = $("#search-input").val();
	if(query=='')
	{
		$(".search-result").hide();
	}
	else
	{
		//Search
		console.log(query);
		//Sending request to server
		let url = `http://localhost:8084/search/${query}`;
		fetch(url).then(response=>{
			return response.json();
		}).then(data=>{
			//Data..
			console.log(data);
			//show data on result div
			let text = `<div class='list-group'>`;
			data.forEach((contact)=>{
				text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'>${contact.firstName}</a>` 
			});
			text += `</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
		});
		
	}
};
	 
