package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
	class InternshipAplicationTests {

		@Autowired
		private ItemRepository itemRepository;

		@Test
		void testItemCreation() {
			Item item = new Item();
			item.setName("Test");
			item.setEmail("test@test.com");
			item.setStatus("NEW");

			Item savedItem = itemRepository.save(item);

			assertNotNull(savedItem.getId()); //verification
			assertEquals("Spring Test", savedItem.getName());
			itemRepository.delete(savedItem);
		}

		@Test
		void testFindNullItem() {
			Optional<Item> foundItem = itemRepository.findById(999L);
			assertFalse(foundItem.isPresent());
		}
	}