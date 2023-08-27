   $(document).ready(function() {
			$("#stats-type").change(function() {
				var selectedType = $(this).val();

				if (selectedType === "category") {
					$("#category-stats").show();
					$("#producer-stats").hide();
				} else if (selectedType === "producer") {
					$("#category-stats").hide();
					$("#producer-stats").show();
				}
			});
		});

