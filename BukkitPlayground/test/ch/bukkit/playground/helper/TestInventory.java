package ch.bukkit.playground.helper;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class TestInventory implements PlayerInventory {

    List<ItemStack> itemStack = new LinkedList<org.bukkit.inventory.ItemStack>();

    @Override
    public ItemStack[] getArmorContents() {
        return null;
    }

    @Override
    public ItemStack getHelmet() {
        return null;
    }

    @Override
    public ItemStack getChestplate() {
        return null;
    }

    @Override
    public ItemStack getLeggings() {
        return null;
    }

    @Override
    public ItemStack getBoots() {
        return null;
    }

    @Override
    public void setArmorContents(ItemStack[] items) {

    }

    @Override
    public void setHelmet(ItemStack helmet) {

    }

    @Override
    public void setChestplate(ItemStack chestplate) {

    }

    @Override
    public void setLeggings(ItemStack leggings) {

    }

    @Override
    public void setBoots(ItemStack boots) {

    }

    @Override
    public ItemStack getItemInHand() {
        return null;
    }

    @Override
    public void setItemInHand(ItemStack stack) {

    }

    @Override
    public int getHeldItemSlot() {
        return 0;
    }

    @Override
    public int clear(int i, int i1) {
        return 0;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(int size) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ItemStack getItem(int index) {
        return null;
    }

    @Override
    public void setItem(int index, ItemStack item) {

    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        return null;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        return null;
    }

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[0];
    }

    @Override
    public void setContents(ItemStack[] items) {

    }

    @Override
    public boolean contains(int materialId) {
        return false;
    }

    @Override
    public boolean contains(Material material) {
        return false;
    }

    @Override
    public boolean contains(ItemStack item) {
        return false;
    }

    @Override
    public boolean contains(int materialId, int amount) {
        return false;
    }

    @Override
    public boolean contains(Material material, int amount) {
        return false;
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        return false;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return null;
    }

    @Override
    public int first(int materialId) {
        return 0;
    }

    @Override
    public int first(Material material) {
        return 0;
    }

    @Override
    public int first(ItemStack item) {
        return 0;
    }

    @Override
    public int firstEmpty() {
        return 0;
    }

    @Override
    public void remove(int materialId) {

    }

    @Override
    public void remove(Material material) {

    }

    @Override
    public void remove(ItemStack item) {

    }

    @Override
    public void clear(int index) {

    }

    @Override
    public void clear() {

    }

    @Override
    public List<HumanEntity> getViewers() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public InventoryType getType() {
        return null;
    }

    @Override
    public HumanEntity getHolder() {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return null;
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        return null;
    }
}
