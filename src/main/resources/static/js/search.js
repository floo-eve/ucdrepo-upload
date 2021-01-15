        // constants
        var elemsearch =  document.getElementById("search");
        console.log(elemsearch);
        
        console.log("test1");

        // uploads the File
        function searchComponents(e) {
            e.preventDefault();
            console.log("test in function");

            const xhr = new XMLHttpRequest();

            xhr.open('POST', "/ucd4u/component/search");

            xhr.addEventListener("error", e => {
                console.log("error");

            });


            xhr.addEventListener("load", event => {
            });


            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status == 200) {
                showSearchResult(xhr.response);
                }
                else if (xhr.status == 401) {
                    window.location.href = "/ucd4u/login";
                }
            }

            xhr.send(new FormData(this));

        }

        function showSearchResult(response) {
            console.log(response);

            var list = "";

            var jsonResponse = JSON.parse(response);
            const actionUrl = document.getElementById("searchForm").getAttribute('action');
            console.log(actionUrl.split("\\"));

            jsonResponse.forEach(element => {
                list = list +`<a class="dropdown-item" href="/ucd4u/${element.url}">${element.name}</a>`;
                
            });

            console.log(list);
            var element = document.getElementById("searchResult");

            if (list.length==0) {
                list=`<a class="dropdown-item" href="#">no results found</a>`;
            }
            
            element.innerHTML = list;
            $('.dropdown-toggle').dropdown("toggle");
            
        }

        document.getElementById("searchForm").addEventListener("submit", searchComponents);